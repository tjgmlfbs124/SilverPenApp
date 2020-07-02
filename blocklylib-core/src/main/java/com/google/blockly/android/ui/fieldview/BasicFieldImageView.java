/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.blockly.android.ui.fieldview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.blockly.android.R;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Renders an image bitmap. The FieldImage source can be any of the following:
 * <ul>
 *     <li>{@code http:} or {@code https:} URL</li>
 *     <li>{@code data:} URI</li>
 *     <li>{@code file:///android_assets/} URL</li>
 *     <li>A relative path in the project's {@code assets/} directory</li>
 * </ul>
 * <p/>
 * Any image format recognized by the Android device's BitmapFactory is valid. Usually this is a
 * {@code .jpg} or {@code .png}.
 */
public class BasicFieldImageView extends AppCompatImageView implements FieldView {
    private static String TAG = "BasicFieldImageView";

    private static final Pattern HTTP_URL_PATTERN = Pattern.compile("https?://.*");
    private static final Pattern DATA_URL_PATTERN = Pattern.compile("data:(.*)");
    private static final String FILE_ASSET_URL_PREFIX = "file:///android_assets/";

    protected final Field.Observer mFieldObserver = new Field.Observer() {
        @Override
        public void onValueChanged(Field field, String newValue, String oldValue) {
            synchronized (mImageFieldLock) {
                if (mImageField == field) {
                    String source = mImageField.getSource();
                    if (source.equals(mImageSrc)) {
                        updateViewSize();
                    } else {
                        startLoadingImage(source);
                    }
                }
            }
        }
    };

    protected FieldImage mImageField;
    protected Object mImageFieldLock = new Object();
    protected String mImageSrc = null;

    /**
     * Constructs a new {@link BasicFieldImageView}.
     *
     * @param context The application's context.
     */
    public BasicFieldImageView(Context context) {
        super(context);
    }

    public BasicFieldImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasicFieldImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @UiThread
    @Override
    public void setField(Field field, boolean isEXE) {
        FieldImage imageField = (FieldImage) field;
        if (mImageField == imageField) {
            return;
        }

        synchronized (mImageFieldLock) {
            if (mImageField != null) {
                mImageField.unregisterObserver(mFieldObserver);
            }
            mImageField = imageField;
            if (mImageField != null) {
                startLoadingImage(mImageField.getSource());
                mImageField.registerObserver(mFieldObserver);
            } else {
                // TODO(#44): Set image to default 'no image' default
            }
        }
    }

    @Override
    public Field getField() {
        return mImageField;
    }

    @Override
    public void unlinkField() {
        setField(null, false);
    }

    /**
     * Asynchronously load and set image bitmap.
     */
    @Deprecated // Use startLoadingImage(String) below. Deprecated 2017 Oct 19.
    protected void startLoadingImage() {
//        startLoadingImage(mImageField.getSource());
    }

    /**
     * Asynchronously load and set image bitmap from the specified source string.
     * @param source The URI source of the image.
     */
    // TODO(#44): Provide a default image if the image loading fails.

    String gif_source = "";
    protected void startLoadingImage(final String source) {

        final BasicFieldImageView bimgview = this;
        /***
         * @auth HTS -> gif 사용법.
         * gif를 위하여 먼저 gif를 다 그리고, 그 후에 asyncTask를 이용해 bitmap을 가져온다
         * gif를 사용하기 위하여 Glide lib을 도입하였는데, asyncTask에서는 사용이 불가!
         * 왜냐하면 main UI Thread를 사용하기 때문. 따라서, gif만 먼저 처리하고 나머지는 asyncTask로 넘긴다.
         */

        final int resourceId = getResources().getIdentifier("@drawable/" + source,"drawable", getContext().getPackageName());
        Log.i("seo", "sourcre => " + source);
        // Do I/O in the background
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    if (source.equals("images/wondering_3.gif")) {
                        gif_source = source;
                        return null;
                    }
                    else{
                        return getBitmapFromVectorDrawable(getContext(), resourceId);
                    }
                } catch (Exception e)
                {
                    Log.i("seo", "[error]get bitmap fail.. => " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    setImageBitmap(bitmap);
                    mImageSrc = source;
                    updateViewSize();
                } else {
                    // TODO(#44): identify and bundle as a resource a suitable default
                    // "cannot load" bitmap.

                    if(gif_source.equals("images/wondering_3.gif"))
                    {
//                        Log.i("HTS", "gif_source => " + gif_source);
                        /** @HTS
                         * 비동기방식으로 이미지로드 함수가 실행되고 액티비티가 바로 종료되면 파라미터로 넘겨준 Activity가 이미 destroy된 상태이기때문에
                         * 발생하는 에러이다 해결방법은 이렇다.
                         *
                         * Glide.with(Activity)가 일반적인 사용방법이므로 Activity를 check해주는 것이 일반적인 방법이지만, 우리 App은
                         * Context check만 해주면 된다.
                         */
                        Activity activity = (Activity) getContext();
                        if(activity.isFinishing())
                            return;

                        Glide.with(bimgview)
                                .load(R.raw.wondering_3_large)
                                .apply(new RequestOptions()
                                        .override(180, 180)
                                )
                                .into(bimgview);

                        gif_source = "";
                    }
                }
                requestLayout();
            }
        }.execute(source);
    }

    @VisibleForTesting
    InputStream getStreamForSource(String source) throws IOException {
        if (HTTP_URL_PATTERN.matcher(source).matches()) {
            return (InputStream) new URL(source).getContent();
        } else if (DATA_URL_PATTERN.matcher(source).matches()) {
            String imageDataBytes = source.substring(source.indexOf(",")+1);
            return new ByteArrayInputStream(
                    Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        } else {
            String assetPath;
            if (source.startsWith(FILE_ASSET_URL_PREFIX)) {
                assetPath = source.substring(FILE_ASSET_URL_PREFIX.length());
            } else if (source.startsWith("/")) {
                assetPath = source.substring(1);
            } else {
                assetPath = source;
            }
            return getContext().getAssets().open(assetPath);
        }
    }

    @UiThread
    protected void updateViewSize() {
        // Check for null b/c of https://groups.google.com/d/msg/blockly/lC91XADUiI4/Y0cLRAYQBQAJ
        // Synchronize ImageField update. Issues #678.
        synchronized (mImageFieldLock) {
            if (mImageField == null) {
                setMinimumWidth(0);
                setMinimumHeight(0);
            } else {
                float density = getContext().getResources().getDisplayMetrics().density;
                int pxWidth = (int) Math.ceil(mImageField.getWidth() * density);
                int pxHeight = (int) Math.ceil(mImageField.getHeight() * density);

                setMinimumWidth(pxWidth);
                setMinimumHeight(pxHeight);
            }
        }
    }
    /**
     * svg to bitmap..
     * @param context getActivity()..
     * @param drawableId R.drawable.id( SVG (.xml) )
     * @return bitmap
     */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
