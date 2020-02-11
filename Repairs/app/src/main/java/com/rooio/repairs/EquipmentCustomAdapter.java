package com.rooio.repairs;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

class EquipmentCustomAdapter implements ListAdapter {
    ArrayList<EquipmentData> arrayList;
    ArrayList<String> locations = new ArrayList<>();
    ArrayList<Button> buttons = new ArrayList<>();
    Context context;

    public EquipmentCustomAdapter(Context context, ArrayList<EquipmentData> equipment) {
        this.arrayList = equipment;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EquipmentData data = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.equipment_list_item, parent, false);

            TextView text = ((Activity)context).findViewById(R.id.equipmentPageNoSelectionText);
            ConstraintLayout equipmentDetails = ((Activity)context).findViewById(R.id.equipmentDetailsConstraint);
            ConstraintLayout equipmentAnalytics = ((Activity)context).findViewById(R.id.analyticsConstraint);

            // displaying locations
            TextView location = convertView.findViewById(R.id.location);
            if(!locations.contains(data.location)){
                location.setText(data.location.toUpperCase());
                locations.add(data.location);
            }
            else
                location.setVisibility(View.GONE);

            // displaying equipment buttons
            Button equipment = convertView.findViewById(R.id.equipmentItem);
            equipment.setText(data.name);

            //adding button to list of existing buttons
            buttons.add(equipment);
            equipment.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    // changing all other buttons back to gray
                    for(Button b : buttons){
                        b.setBackgroundResource(R.drawable.dark_gray_button_border);
                        b.setTextColor(Color.parseColor("#747479"));
                    }

                    equipment.setBackgroundResource(R.drawable.green_button_border);
                    equipment.setTextColor(Color.parseColor("#00CA8F"));

                    // getting rid of "select an equipment" text
                    text.setVisibility(v.GONE);

                    // displaying equipment details
                    equipmentDetails.setVisibility(v.VISIBLE);
                    equipmentDetails(data, equipmentDetails);

                    // displaying equipment analytics
                    equipmentAnalytics.setVisibility(v.VISIBLE);
                }
            });
        }
        return convertView;
    }

    public void equipmentDetails(EquipmentData equipment, ConstraintLayout constraintLayout){
        TextView displayName = constraintLayout.findViewById(R.id.displayName);
        displayName.setText(equipment.name);

        TextView serialNumber = constraintLayout.findViewById(R.id.serialNumber);
        serialNumber.setText(equipment.serialNumber);

        TextView lastServiceDate = constraintLayout.findViewById(R.id.lastServiceDate);
        lastServiceDate.setText(equipment.lastServiceDate);

        TextView manufacturer = constraintLayout.findViewById(R.id.manufacturer);
        manufacturer.setText(equipment.manufacturer);

        TextView location = constraintLayout.findViewById(R.id.location);
        location.setText(equipment.location);

        TextView modelNum = constraintLayout.findViewById(R.id.modelNumber);
        modelNum.setText(equipment.modelNumber);

        TextView lastServiceBy = constraintLayout.findViewById(R.id.lastServiceBy);
        lastServiceBy.setText(equipment.lastServiceBy);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
}