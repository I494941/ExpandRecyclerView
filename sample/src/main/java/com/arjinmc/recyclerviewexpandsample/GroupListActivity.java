package com.arjinmc.recyclerviewexpandsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewGroupAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewGroupTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.recyclerviewdecoration.RecyclerViewItemDecoration;
import com.arjinmc.recyclerviewexpandsample.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sample for group list
 * Created by Eminem Lo on 2017/9/11.
 * email: arjinmc@hotmail.com
 */

public class GroupListActivity extends AppCompatActivity {

    private final int OPTION_ADD = 1;
    private final int OPTION_REMOVE = 2;
    private final int OPTION_CHANGE = 3;
    private final int OPTION_MOVE = 4;

    private int mOption;
    private int mNum = -1;

    private RadioGroup mRgOption;
    private EditText mEtPosition;
    private EditText mEtGroupPosition;
    private EditText mEtChildPosition;
    private EditText mEtItemCount;
    private EditText mEtCarType;

    private EditText mEtToPosition;
    private LinearLayout mLLtoPosition;
    private EditText mEtToGroupPosition;
    private EditText mEtToChildPosition;

    private RecyclerViewGroupAdapter mGroupAdapter;
    private List<Car> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demo);
        getSupportActionBar().setSubtitle("GroupList");

        mRgOption = (RadioGroup) findViewById(R.id.rg_mode);
        mEtPosition = (EditText) findViewById(R.id.et_position);
        mEtGroupPosition = (EditText) findViewById(R.id.et_groupPosition);
        mEtChildPosition = (EditText) findViewById(R.id.et_childPosition);
        mEtItemCount = (EditText) findViewById(R.id.et_itemCount);
        mEtCarType = (EditText) findViewById(R.id.et_carType);

        mEtToPosition = (EditText) findViewById(R.id.et_toPosition);
        mLLtoPosition = (LinearLayout) findViewById(R.id.ll_toPosition);
        mEtToGroupPosition = (EditText) findViewById(R.id.et_toGroupPosition);
        mEtToChildPosition = (EditText) findViewById(R.id.et_toChildPosition);

        mRgOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_add:
                        mOption = OPTION_ADD;
                        showToPosition(false);
                        break;
                    case R.id.rb_remove:
                        mOption = OPTION_REMOVE;
                        showToPosition(false);
                        break;
                    case R.id.rb_change:
                        mOption = OPTION_CHANGE;
                        showToPosition(false);
                        break;
                    case R.id.rb_move:
                        mOption = OPTION_MOVE;
                        showToPosition(true);
                        break;
                }
            }
        });

        RecyclerView rvList = (RecyclerView) findViewById(R.id.rv_list);
        RecyclerViewStyleHelper.toLinearLayout(rvList, LinearLayout.VERTICAL);
        rvList.addItemDecoration(new RecyclerViewItemDecoration.Builder(this)
                .color(Color.GRAY)
                .thickness(3)
                .create());

        mDataList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Car car = new Car();
            car.setBrand("Car Band" + i);
            car.setTypeName("Car Type" + i * i);
            if (i % 10 == 0) {
                car.setGroup("Group " + (i / 10));
            }
            mDataList.add(car);
        }

        mGroupAdapter = new RecyclerViewGroupAdapter<>(this, mDataList
                , new int[]{R.layout.item_group_type, R.layout.item_list_type1}
                , new RecyclerViewGroupTypeProcessor<Car>() {

            @Override
            public void onBindGroupViewHolder(RecyclerViewViewHolder holder, int groupPosition, Car car) {
                TextView tvGroup = holder.getView(R.id.tv_group);
                tvGroup.setText(car.getGroup());
            }

            @Override
            public void onBindItemViewHolder(RecyclerViewViewHolder holder, final int groupPosition, final int itemPosition, Car car) {
                TextView tvContent = holder.getView(R.id.tv_content);
                tvContent.setText("Car brand:" + car.getBrand() + " / type: " + car.getTypeName());

                tvContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(GroupListActivity.this
                                , "Group: " + groupPosition + "\titemPosition: " + itemPosition
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public int getItemViewType(int position) {
                if (mDataList.get(position).getGroup() != null)
                    return 0;
                return 1;
            }
        });

        rvList.setAdapter(mGroupAdapter);
        mOption = OPTION_ADD;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                updateAdapter();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAdapter() {
        switch (mOption) {
            case OPTION_ADD:
                addCar();
                break;
            case OPTION_REMOVE:
                removeCar();
                break;
            case OPTION_CHANGE:
                changeCar();
                break;
            case OPTION_MOVE:
                moveCar();
                break;
        }

    }

    /**
     * add car
     */
    private void addCar() {

        int position = getSpecificPosition(mEtPosition);
        int groupPosition = getSpecificPosition(mEtGroupPosition);
        int childPosition = getSpecificPosition(mEtChildPosition);
        int itemCount = getSpecificPosition(mEtItemCount);

        if (position != -1 && itemCount != -1) {

            for (int i = 0; i < itemCount; i++) {
                mDataList.add(position + i, createCar());
            }
            mGroupAdapter.notifyDataRangeInserted(position, itemCount);

        } else if (position != -1) {

            mDataList.add(position, createCar());
            mGroupAdapter.notifyDataInserted(position);

        } else if (groupPosition != -1 && childPosition != -1 && itemCount != -1) {

            position = mGroupAdapter.getItemInDataListPosition(groupPosition, childPosition);
            for (int i = 0; i < itemCount; i++) {
                mDataList.add(position + i, createCar());
            }
            mGroupAdapter.notifyDataRangeInserted(groupPosition, childPosition, itemCount);

        } else if (groupPosition != -1 && childPosition != -1) {

            mDataList.add(mGroupAdapter.getItemInDataListPosition(groupPosition, childPosition), createCar());
            mGroupAdapter.notifyDataInserted(groupPosition, childPosition);

        }

        mGroupAdapter.notifyDataChanged();

    }

    /**
     * remove car
     */
    private void removeCar() {

        int position = getSpecificPosition(mEtPosition);
        int groupPosition = getSpecificPosition(mEtGroupPosition);
        int childPosition = getSpecificPosition(mEtChildPosition);
        int itemCount = getSpecificPosition(mEtItemCount);

        if (position != -1 && itemCount != -1) {

            for (int i = itemCount - 1; i >= 0; i--) {
                mDataList.remove(position + i);
            }
            mGroupAdapter.notifyDataRangeRemoved(position, itemCount);

        } else if (position != -1) {

            mDataList.remove(position);
            mGroupAdapter.notifyDataRemoved(position);

        } else if (groupPosition != -1 && childPosition != -1 && itemCount != -1) {

            position = mGroupAdapter.getItemInDataListPosition(groupPosition, childPosition);
            for (int i = itemCount - 1; i >= 0; i--) {
                mDataList.remove(position + i);
            }
            mGroupAdapter.notifyDataRangeRemoved(groupPosition, childPosition, itemCount);

        } else if (groupPosition != -1 && childPosition != -1) {

            mDataList.remove(mGroupAdapter.getItemInDataListPosition(groupPosition, childPosition));
            mGroupAdapter.notifyDataRemoved(groupPosition, childPosition);

        }

        mGroupAdapter.notifyDataChanged();

    }

    /**
     * change car info
     */
    private void changeCar() {

        int position = getSpecificPosition(mEtPosition);
        int groupPosition = getSpecificPosition(mEtGroupPosition);
        int childPosition = getSpecificPosition(mEtChildPosition);
        int itemCount = getSpecificPosition(mEtItemCount);

        if (position != -1 && itemCount != -1) {

            for (int i = 0; i < itemCount; i++) {
                Car car = mDataList.get(position + i);
                car.setTypeName(getCarType());
                mDataList.set(position + i, car);
            }
            mGroupAdapter.notifyDataRangeChanged(position, itemCount);

        } else if (position != -1) {

            Car car = mDataList.get(position);
            car.setTypeName(getCarType());
            mDataList.set(position, car);
            mGroupAdapter.notifyDataChanged(position);

        } else if (groupPosition != -1 && childPosition != -1 && itemCount != -1) {

            position = mGroupAdapter.getItemInDataListPosition(groupPosition, childPosition);
            for (int i = 0; i < itemCount; i++) {
                Car car = mDataList.get(position + i);
                car.setTypeName(getCarType());
                mDataList.set(position + i, car);
            }
            mGroupAdapter.notifyDataRangeChanged(groupPosition, childPosition, itemCount);

        } else if (groupPosition != -1 && childPosition != -1) {

            position = mGroupAdapter.getItemInDataListPosition(groupPosition, childPosition);
            Car car = mDataList.get(position);
            car.setTypeName(getCarType());
            mDataList.set(mGroupAdapter.getItemInDataListPosition(groupPosition, childPosition), car);
            mGroupAdapter.notifyDataChanged(groupPosition, childPosition);

        }

        mGroupAdapter.notifyDataChanged();

    }

    /**
     * move car
     */
    private void moveCar() {
        int from = getSpecificPosition(mEtPosition);
        int to = getSpecificPosition(mEtToPosition);

        int fromGroupPosition = getSpecificPosition(mEtGroupPosition);
        int fromChildPosition = getSpecificPosition(mEtChildPosition);
        int toGroupPosition = getSpecificPosition(mEtToGroupPosition);
        int toChildPosition = getSpecificPosition(mEtToChildPosition);

        if (fromGroupPosition != -1 && fromChildPosition != -1
                && toGroupPosition != -1 && toChildPosition != -1) {

            from = mGroupAdapter.getItemInDataListPosition(fromGroupPosition, fromChildPosition);
            to = mGroupAdapter.getItemInDataListPosition(toGroupPosition, toChildPosition);

            Car tempCar = mDataList.get(from);
            if (from - to > 0) {
                mDataList.remove(from);
                mDataList.add(to, tempCar);
            } else if (from - to < 0) {
                mDataList.add(to, tempCar);
                mDataList.remove(from);
            }
            mGroupAdapter.notifyDataMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
        } else {

            Car tempCar = mDataList.get(from);
            if (from - to > 0) {
                mDataList.remove(from);
                mDataList.add(to, tempCar);
            } else if (from - to < 0) {
                mDataList.add(to, tempCar);
                mDataList.remove(from);
            }
            mGroupAdapter.notifyDataMoved(from, to);
        }
        mGroupAdapter.notifyDataChanged();
    }

    /**
     * create a  car object
     *
     * @return
     */
    private Car createCar() {
        Car car = new Car();
        car.setUuid(UUID.randomUUID().toString());
        car.setTypeName(getCarType());
        car.setBrand("Car Band Unknown");
        return car;
    }

    /**
     * get car type
     *
     * @return string
     */
    private String getCarType() {
        String carType = mEtCarType.getText().toString();
        if (TextUtils.isEmpty(carType)) {
            return "test car type" + (++mNum);
        } else return carType;

    }

    /**
     * get the value of specific edittext
     *
     * @param editText
     * @return int
     */
    private int getSpecificPosition(EditText editText) {
        int position = -1;
        String positionString = editText.getText().toString();
        if (!TextUtils.isEmpty(positionString))
            position = Integer.valueOf(positionString);
        return position;
    }

    /**
     * control if need to show views for move mode
     *
     * @param shown true is shown
     */
    private void showToPosition(boolean shown) {

        if (shown) {
            mEtToPosition.setVisibility(View.VISIBLE);
            mLLtoPosition.setVisibility(View.VISIBLE);
        } else {
            mEtToPosition.setVisibility(View.GONE);
            mLLtoPosition.setVisibility(View.GONE);
        }
    }
}