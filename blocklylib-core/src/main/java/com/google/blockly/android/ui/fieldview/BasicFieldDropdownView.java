/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.blockly.android.ui.fieldview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.blockly.android.R;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldDropdown;

import java.util.List;

/**
 * Renders a dropdown field as part of a Block.
 */
public class BasicFieldDropdownView extends Spinner implements FieldView {
    private static final String TAG = "BasicFieldDropdownView";

    private Field.Observer mFieldObserver = new Field.Observer() {
        @Override
        public void onValueChanged(Field field, String oldValue, String newValue) {
            setSelection(mDropdownField.getSelectedIndex());
        }
    };

    protected FieldDropdown mDropdownField;
    protected int mItemLayout;
    protected int mItemDropdownLayout;

    /**
     * Constructs a new {@link BasicFieldDropdownView}.
     *
     * @param context The application's context.
     */
    public BasicFieldDropdownView(Context context) {
        super(context);
        loadAttributes(null, 0);
    }

    public BasicFieldDropdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs, 0);
    }

    public BasicFieldDropdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(attrs, defStyleAttr);
    }

    private void loadAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BasicFieldDropdownView, defStyleAttr, 0);

        try {
            mItemLayout = a.getResourceId(R.styleable.BasicFieldDropdownView_itemLayout, android.R.layout.simple_spinner_item);
            mItemDropdownLayout = a.getResourceId(R.styleable.BasicFieldDropdownView_dropdownItemLayout, android.R.layout.simple_spinner_dropdown_item);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void setField(Field field, boolean isEXE) {
        FieldDropdown dropdownField = (FieldDropdown) field;
        if (mDropdownField == dropdownField) {
            return;
        }

        if (mDropdownField != null) {
            mDropdownField.unregisterObserver(mFieldObserver);
        }
        mDropdownField = dropdownField;
        if (mDropdownField != null) {
            List<String> items = mDropdownField.getDisplayNames();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), mItemLayout, items);

            // (Spinner - dropdown)item style custom... @HTS ( variables는 별도임. )
            // lib-vertical 밑의 VerticalBlockViewFactory의 setDropDownViewResource에서 custom으로 설정하여준다.
//            adapter.setDropDownViewResource(mItemDropdownLayout);
            adapter.setDropDownViewResource(R.layout.custom_spinner_item);
            setAdapter(adapter);

            Log.i("HTS", "items => " + items);
            Log.i("HTS", "items => " + items.size());
            Log.i("HTS", "this => " + this + "");

            // dropdown 가로 사이즈를 늘려준다 ( ex) tone_block )
//            this.setDropDownWidth(150);

            // dropdown 각 item들의 정렬기준.
//            this.setTextAlignment(TEXT_ALIGNMENT_CENTER);


            for(int i = 0; i < items.size(); i++)
            {
                Log.i("HTS", "items[" + i + "] => " + items.get(i));
            }

            if (items.size() > 0) {
                setSelection(mDropdownField.getSelectedIndex());
            }

            mDropdownField.registerObserver(mFieldObserver);
        } else {
            setSelection(0);
            setAdapter(null);
        }
    }

    @Override
    public Field getField() {
        return mDropdownField;
    }

    @Override
    public void setSelection(int position) {
        if (position == getSelectedItemPosition()) {
            return;
        }
        super.setSelection(position);
        if (mDropdownField != null) {
            mDropdownField.setSelectedIndex(position);
        }
    }

    @Override
    public void unlinkField() {
        setField(null, false);
    }
}
