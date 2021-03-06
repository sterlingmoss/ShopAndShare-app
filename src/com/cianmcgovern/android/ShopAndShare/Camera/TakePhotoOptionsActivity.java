/*******************************************************************************
 * Copyright 2012 Cian Mc Govern
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cianmcgovern.android.ShopAndShare.Camera;

import java.util.ArrayList;

import com.cianmcgovern.android.ShopAndShare.CheckFeatures;
import com.cianmcgovern.android.ShopAndShare.HelpDialog;
import com.cianmcgovern.android.ShopAndShare.R;
import com.cianmcgovern.android.ShopAndShare.ShopAndShare;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Displays options for the camera to the user and allows the user to change
 * them to suit their needs
 * 
 * @author Cian Mc Govern <cian@cianmcgovern.com>
 * 
 */
public class TakePhotoOptionsActivity extends ExpandableListActivity {

    private ArrayList<TakePhotoOption> mOptions = new ArrayList<TakePhotoOption>();

    private OptionsListAdapter mAdapter;

    // Constants for storing the main options
    private final String mFocusMode = "Focus Mode";
    private final String mFlashMode = "Flash Mode";
    private final String mSceneMode = "Scene Mode";
    private final String mWhiteBalance = "White Balance";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // We put our main options and their specific children into
        // TakePhotoOption objects
        ArrayList<String> optionsChildren = new ArrayList<String>();

        optionsChildren.add(Camera.Parameters.FOCUS_MODE_AUTO);
        optionsChildren.add(Camera.Parameters.FOCUS_MODE_MACRO);
        optionsChildren.add(Camera.Parameters.FOCUS_MODE_FIXED);
        mOptions.add(new TakePhotoOption(mFocusMode, optionsChildren));

        optionsChildren = new ArrayList<String>();
        optionsChildren.add(Camera.Parameters.FLASH_MODE_AUTO);
        optionsChildren.add(Camera.Parameters.FLASH_MODE_OFF);
        optionsChildren.add(Camera.Parameters.FLASH_MODE_ON);
        optionsChildren.add(Camera.Parameters.FLASH_MODE_TORCH);
        mOptions.add(new TakePhotoOption(mFlashMode, optionsChildren));

        optionsChildren = new ArrayList<String>();
        optionsChildren.add(Camera.Parameters.SCENE_MODE_AUTO);
        optionsChildren.add(Camera.Parameters.SCENE_MODE_BARCODE);
        optionsChildren.add(Camera.Parameters.SCENE_MODE_NIGHT);
        optionsChildren.add(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
        mOptions.add(new TakePhotoOption(mSceneMode, optionsChildren));

        optionsChildren = new ArrayList<String>();
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_AUTO);
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_SHADE);
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_TWILIGHT);
        optionsChildren.add(Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT);
        mOptions.add(new TakePhotoOption(mWhiteBalance, optionsChildren));

        // Use our own ListAdapter that extends the BaseExtendableListAdapter
        mAdapter = new OptionsListAdapter();
        setListAdapter(mAdapter);
        this.getExpandableListView().setBackgroundResource(
                R.drawable.default_background);
        this.getExpandableListView().setCacheColorHint(00000000);
        registerForContextMenu(getExpandableListView());
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ShopAndShare.sContext, TakePhoto.class);
        startActivity(i);
        finish();
    }

    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.layout.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.exit:
            Intent i = new Intent(ShopAndShare.sContext, TakePhoto.class);
            startActivity(i);
            finish();
            return true;
        case R.id.help:
            new HelpDialog(this, this.getText(
                    R.string.takePhotoOptionsHelpMessage).toString()).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Our OptionsListAdapter extends the BaseExpandableListAdapter.
     * 
     * Each group item is set to the main options specified above. These are
     * obtained from the ArrayList mOptions which contains all of our
     * TakePhotoOption objects. The childView is populated with the children by
     * calling getOptionChildren on the TakePhotoOption object.
     * 
     * Each childView is a checkbox that toggles whether the option is selected
     * or not. The booleans mFocusCheck, mFlashCheck & mSceneCheck prevent more
     * than one option being selected in the same childView.
     * 
     * @author Cian Mc Govern <cian@cianmcgovern.com>
     * 
     */
    private class OptionsListAdapter extends BaseExpandableListAdapter {

        // These booleans prevent more than one option being selected in the
        // same child view
        private boolean mFocusCheck;
        private boolean mFlashCheck;
        private boolean mSceneCheck;
        private boolean mWhiteBalanceCheck;

        /**
         * Creates a TextView with layout parameters based on the parent
         * 
         * @return TextView
         */
        private TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);
            TextView textView = new TextView(TakePhotoOptionsActivity.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(64, 0, 0, 0);
            return textView;
        }

        /**
         * Creates a CheckBox with layout parameters based on the parent
         * 
         * @return CheckBox
         */
        private CheckBox getCheckBox() {
            CheckBox cb = new CheckBox(TakePhotoOptionsActivity.this);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);
            cb.setLayoutParams(lp);
            cb.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            cb.setPadding(64, 0, 0, 0);
            return cb;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mOptions.get(groupPosition).getOptionChildren()
                    .get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {

            // This childView will contain a checkbox with text matching the
            // child option at getOptionChildren().get(childPosition)
            final CheckBox cb = getCheckBox();
            TakePhotoOption option = (TakePhotoOption) getGroup(groupPosition);
            final String optionText = option.getOption();
            final String optionChild = option.getOptionChildren().get(
                    childPosition);

            if (optionText.equals(mFlashMode) && !CheckFeatures.haveFlash())
                cb.setEnabled(false);

            else if (optionText.equals(mFocusMode)
                    && !CheckFeatures.haveAutoFocus())
                cb.setEnabled(false);

            else {
                // Make this checkbox checked if option is selected in TakePhoto
                if (optionText.equals(mFocusMode)
                        && optionChild.equals(TakePhoto.sFocusMode)) {
                    cb.setChecked(true);
                    mFocusCheck = true;
                }

                else if (optionText.equals(mFlashMode)
                        && optionChild.equals(TakePhoto.sFlashMode)) {
                    cb.setChecked(true);
                    mFlashCheck = true;
                }
            }

            if (optionText.equals(mSceneMode)
                    && optionChild.equals(TakePhoto.sSceneMode)) {
                cb.setChecked(true);
                mSceneCheck = true;
            }

            else if (optionText.equals(mWhiteBalance)
                    && optionChild.equals(TakePhoto.sWhiteBalance)) {
                cb.setChecked(true);
                mWhiteBalanceCheck = true;
            }

            cb.setText(optionChild.toUpperCase());

            // We use an OnCheckedChangeListener to set the option in TakePhoto
            cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {

                    // If it is checked, we check what group this child belongs
                    // to and then we see if no other childs have been checked
                    // If not, we can set the particular option in TakePhoto
                    // If there is already a checkbox checked in the same group,
                    // we change the state of the checkbox to not checked
                    if (cb.isChecked()) {
                        if (optionText.equals(mFocusMode)) {
                            if (mFocusCheck)
                                cb.setChecked(false);
                            else {
                                TakePhoto.sFocusMode = cb.getText().toString()
                                        .toLowerCase();
                                mFocusCheck = true;
                            }
                        }

                        else if (optionText.equals(mFlashMode)) {
                            if (mFlashCheck)
                                cb.setChecked(false);
                            else {
                                TakePhoto.sFlashMode = cb.getText().toString()
                                        .toLowerCase();
                                mFlashCheck = true;
                            }

                        }

                        else if (optionText.equals(mSceneMode)) {
                            if (mSceneCheck)
                                cb.setChecked(false);
                            else {
                                TakePhoto.sSceneMode = cb.getText().toString()
                                        .toLowerCase();
                                mSceneCheck = true;
                            }
                        }

                        else if (optionText.equals(mWhiteBalance)) {
                            if (mWhiteBalanceCheck)
                                cb.setChecked(false);
                            else {
                                TakePhoto.sWhiteBalance = cb.getText()
                                        .toString().toLowerCase();
                                mWhiteBalanceCheck = true;
                            }
                        }
                    }
                    // If the user unchecked the option, we set the booleans to
                    // false for the group the option belongs to
                    else {
                        if (optionText.equals(mFocusMode))
                            mFocusCheck = false;
                        else if (optionText.equals(mFlashMode))
                            mFlashCheck = false;
                        else if (optionText.equals(mSceneMode))
                            mSceneCheck = false;
                        else if (optionText.equals(mWhiteBalance))
                            mWhiteBalanceCheck = false;
                    }

                }
            });
            return cb;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mOptions.get(groupPosition).getOptionChildren().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mOptions.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return mOptions.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        // GroupView is a TextView with text matching the option category ie.
        // focus mode, flash mode etc.
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            TextView tv = getGenericView();
            tv.setText(mOptions.get(groupPosition).getOption());
            return tv;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
}
