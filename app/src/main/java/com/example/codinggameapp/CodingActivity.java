package com.example.codinggameapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codinggameapp.Dialog.MissionDialog;
import com.example.codinggameapp.Http.HttpPostRequestClass;
import com.example.codinggameapp.Utils.DataManager;
import com.example.codinggameapp.Utils.ScalableLayout;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.codegen.LanguageDefinition;
import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.android.control.CodeEventInterface;
import com.google.blockly.android.ui.TrashCanView;
import com.google.blockly.model.DefaultBlocks;

import org.json.JSONObject;
import org.liquidplayer.webkit.javascriptcore.JSBaseArray;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class CodingActivity extends AbstractBlocklyActivity implements CodeEventInterface  {

    private BlocklyController mBlocklyController;
    private Handler mHandler;

    private TrashCanView trashCan;

    private String mNoCodeText;
    private TextView mGeneratedTextView;

    private static final String SAVE_FILENAME = "lua_workspace.xml";
    private static final String AUTOSAVE_FILENAME = "lua_workspace_temp.xml";
    private static final List<String> BLOCK_DEFINITIONS = DefaultBlocks.getAllBlockDefinitions();
    private static final List<String> LUA_GENERATORS = Arrays.asList();
    private ImageView CodeEXEBtn;
    private String isMission, mode;

    private boolean isRun = false;
    private int missionClearLevel = 0;
    MissionManageClass missionManager;

    Handler m_handler;
    Runnable m_handlerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            isMission =  bundle.getString("mission", "");
            mode = bundle.getString("mode", "");
        }
        else
            isMission = null;


        missionManager = new MissionManageClass(getApplicationContext(), mode);
        mHandler = new Handler();

        CodeEXEBtn = (ImageView) findViewById(R.id.blockly_code_exe_button);
        mBlocklyController = getController();
        mBlocklyController.setCodeEventInterface(this);
        mGeneratedTextView.setVisibility(View.GONE); // @SEO 코딩화면 옆에 코딩창

        trashCan = (TrashCanView)findViewById(R.id.blockly_trash_icon);
        trashCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearWorkspace();
            }
        });
        CodeEXEBtn.setOnClickListener(new ButtonClickListener());

        Log.i("seo","isMission : " + isMission);
        onClearWorkspace();
    }

    public class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            String robot;
            robot = DataManager.getSaveConnectedRobot(getApplicationContext());
            if (robot.equals("")) {
                Toast.makeText(getApplicationContext(),"로봇과 블루투스 연결이 필요합니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                if(isRun == true) Toast.makeText(CodingActivity.this,"실행중입니다.",Toast.LENGTH_SHORT).show();
                else{
                    isRun = true;
                    // 블루투스 정보를 가지고, 데이터를 전송한다.
                    String Code = mGeneratedTextView.getText().toString();
                    Log.i("seo","Code : " + Code);
                    final JSBaseArray definitionArray = CreateToJavasource(Code);
                    m_handler = new Handler();
                    m_handlerTask = new Runnable()
                    {
                        @Override
                        public void run() {
                            if(definitionArray.size() > 0){
                                missionManager.runBuffer((JSValue)definitionArray.get(0));
                                (definitionArray).remove(0);
                                m_handler.postDelayed(this,1000);
                            }
                            else{
                                BluetoothSendManager.sendProtocol("ff00000023");
                                if(isMission == null) {
                                    isRun = false;
                                    return;
                                }
                                missionClearLevel = missionManager.completeCheck();
                                AlertDialog.Builder builder = new AlertDialog.Builder(CodingActivity.this);
                                builder.setTitle("알림");

//                                if(missionClearLevel != 0){ // 정답. 서버로 점수 전송
                                    new requestMissionResult().execute("http://" + DataManager.connectURL + "/submitMission");
                                    builder.setMessage("다음으로 넘어가볼까?");
                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(mode.equals("barcode")){
                                                Intent intent = new Intent(CodingActivity.this, JoystickActivity.class);
                                                startActivity(intent);
                                            }
                                            // 다음
                                        }
                                    });
                                    builder.setNegativeButton("종료", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
//                                }
//                                else{ // 오답
//                                    builder.setMessage("틀렸어요 ! \n 다시한번 생각해봅시다.");
//                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            onClearWorkspace();
//                                        }
//                                    });
//                                }
                                isRun = false;
                                builder.show();
                                m_handler.removeCallbacks(m_handlerTask);
                                mHandler.removeCallbacksAndMessages(null);
                                missionManager.initObject(mode); // 미션 관련된 정보(오브젝트 XY, 사용블록리스트 등등)들 초기화
                            }
                        }
                    };
                }
                m_handlerTask.run();
            }catch (Exception e){
                Log.i("seo","Send Error : " + e);
            }
        }
    }

    public JSBaseArray CreateToJavasource(String Code){
        String str1 = "var serialArr = new Array();\n";                                             // 1. 로봇으로 전송할 protocol Array를 미리 생성
        str1 += "var definitionArray = new Array();\n";
        Code = str1 + Code;
        Log.i("seo","Code : " + Code);
        JSContext jsContext = new JSContext();                                                      // 2. JS소스에서 Array 변수 값을 추출하기 위해 사용한 JSContext 클래스 정의
        jsContext.evaluateScript(Code);                                                             // 3. JS소스를 실행한다.
        final JSBaseArray blockDefinitionArray = jsContext.property("definitionArray").toJSArray(); // 4. JS소스를 실행하고 'definitionArray' 변수의 값을 추출한다.
        return blockDefinitionArray;
    }

    public void openMissionDialog(String mission){
        Log.i("seo","mode : " + mode);
        if(mode == null) {
            Log.i("seo","null");
            return;
        }
        MissionDialog dialog = new MissionDialog(this);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ScalableLayout.LayoutParams.MATCH_PARENT, ScalableLayout.LayoutParams.MATCH_PARENT);
        dialog.setDialogConfig(mission);
    }

    public class requestMissionResult extends AsyncTask<String, String, String> {
        String mission;
        int clearLevel;
        JSONObject jsonObject = new JSONObject();
        @Override
        protected void onPreExecute() {
            this.mission =  MissionManageClass.mission;
            this.clearLevel = missionClearLevel;
            try{
                String userInfo = DataManager.getSharedPreferences_UserInfo(getApplicationContext());
                JSONObject userJson = new JSONObject(userInfo);
                jsonObject.accumulate("user_id", userJson.getString("user_id"));
                jsonObject.accumulate("user_name", userJson.getString("user_name"));
                jsonObject.accumulate("mission", this.mission);
                jsonObject.accumulate("clearLevel", this.clearLevel);
            }
            catch (Exception e){
                Log.i("seo","json object Error : " + e);
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            HttpPostRequestClass requestClass = new HttpPostRequestClass();
            result = requestClass.request(urls[0], jsonObject); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("seo","mission SUBMIT result : " + result);
        }
    }

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
        new CodeGenerationRequest.CodeGeneratorCallback() {
            @Override
            public void onFinishCodeGeneration(final String generatedCode) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                            mGeneratedTextView.setText(generatedCode);
                            updateTextMinWidth();
                    }
                });
            }
        };

    @Override
    public void OnCodeChange() {
        onRunCode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** @HTS
         * Resume에서 code generate를 하는 이유
         * workspace에 block들이 존재하고, 시작시에 이 block들을 load하는 경우에는 code가 generate되지 않는다.
         *          *          * Activity가 화면에 표시되기 전 단계인 'Resume'단계에서 이 block들을 토대로 code를 generate한다.
         */
        onRunCode();
    }

    @Override
    protected View onCreateContentView(int containerId) {
//        return super.onCreateContentView(containerId);
        View root = getLayoutInflater().inflate(R.layout.activity_coding, null);
        mGeneratedTextView = (TextView) root.findViewById(R.id.generated_code);
        updateTextMinWidth();

        mNoCodeText = mGeneratedTextView.getText().toString(); // Capture initial value.

        return root;
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
//        Log.i("seo " , " BLOCK_DEFINITIONS : " + BLOCK_DEFINITIONS);
        // TODO 191025 여기서 미션레벨 inputExtra 받아서 미션에 맞는 블록 나오도록 설정하기.
        return BLOCK_DEFINITIONS;
    }

    @NonNull
    @Override
    protected LanguageDefinition getBlockGeneratorLanguage() {
        return super.getBlockGeneratorLanguage();
        /** @HTS
         * Language를 바꾸고 싶으면 아래처럼 바꾸면 된다
         * @return 원하는 언어 define. new LanguageDefinition("lua/lua_compressed.js", "Blockly.Lua");
         */
//        return LUA_LANGUAGE_DEF;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        /**
         * @SEO
         * 코딩화면을 열때 기본 카테고리를 바꾸싶을땐 여기서 !
         */
        Bundle getBundle = getIntent().getExtras();

        if(getBundle == null){
            return "default/toolbox_all.xml";
        }
        else{
            String getCategory = getBundle.getString("category", "default/toolbox_all.xml");
            String getMission = getBundle.getString("mission", "");
            String getMode = getBundle.getString("mode", "");

            if(!getMode.equals("barcode")){
                MissionManageClass.mission = getMission;
                openMissionDialog(getMission);
            }
            return getCategory;
        }
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return LUA_GENERATORS;
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        // Uses the same callback for every generation call.
        return mCodeGeneratorCallback;
    }

//    @NonNull
//    @Override
//    protected BluetoothGattCallback getBluetoothGattCallback() {
//        return mBluetoothGattCallback;
//    }

    @Override
    public void onClearWorkspace() {
        super.onClearWorkspace();
        mGeneratedTextView.setText(mNoCodeText);
        updateTextMinWidth();
    }

    /**
     * Estimate the pixel size of the longest line of text, and set that to the TextView's minimum
     * width.
     */
    private void updateTextMinWidth() {
        String text = mGeneratedTextView.getText().toString();
        int maxline = 0;
        int start = 0;
        int index = text.indexOf('\n', start);
        while (index > 0) {
            maxline = Math.max(maxline, index - start);
            start = index + 1;
            index = text.indexOf('\n', start);
        }
        int remainder = text.length() - start;
        if (remainder > 0) {
            maxline = Math.max(maxline, remainder);
        }

        float density = getResources().getDisplayMetrics().density;
        mGeneratedTextView.setMinWidth((int) (maxline * 13 * density));
    }

    /**
     * Optional override of the save path, since this demo Activity has multiple Blockly
     * configurations.
     * @return Workspace save path used by this Activity.
     */
    @Override
    @NonNull
    protected String getWorkspaceSavePath() {
        return SAVE_FILENAME;
    }

    /**
     * Optional override of the auto-save path, since this demo Activity has multiple Blockly
     * configurations.
     * @return Workspace auto-save path used by this Activity.
     */
    @Override
    @NonNull
    protected String getWorkspaceAutosavePath() {
        return AUTOSAVE_FILENAME;
    }
}
