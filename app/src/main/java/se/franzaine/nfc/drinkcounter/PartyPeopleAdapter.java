package se.franzaine.nfc.drinkcounter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PartyPeopleAdapter extends RecyclerView.Adapter<PartyPeopleAdapter.ViewHolder>  {
    private List<String> partyPeopleList;
    Map<String, PartyPerson> partyPeopleMap;

    public PartyPeopleAdapter() {
        partyPeopleList = Collections.emptyList();
        partyPeopleMap = Collections.emptyMap();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(android.R.layout.simple_list_item_2, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String key = partyPeopleList.get(position);
        holder.textView.setText(partyPeopleMap.get(key).getName());
        holder.secondRow.setText("Drinks left: " + String.valueOf(partyPeopleMap.get(key).getDrinksLeft()));
    }

    @Override
    public int getItemCount() {
        return partyPeopleList.size();
    }

    public void setNewList(List<String> partyPeopleList, Map<String, PartyPerson> partyPeopleMap) {
        this.partyPeopleList = partyPeopleList;
        this.partyPeopleMap = partyPeopleMap;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView secondRow;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(android.R.id.text1);
            secondRow = (TextView) v.findViewById(android.R.id.text2);
        }
    }
}
