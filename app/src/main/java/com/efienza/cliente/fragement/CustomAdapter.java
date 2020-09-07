package com.efienza.cliente.fragement;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.efienza.cliente.R;
import java.util.ArrayList;
public class CustomAdapter extends ArrayAdapter {
    private ArrayList dataSet;
    Context mContext;
    // View lookup cache
    private static class ViewHolder {
        TextView txtcDireccion;

    }
    public CustomAdapter(ArrayList data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return dataSet.size();
    }
    @Override
    public DataModel getItem(int position) {
        return (DataModel) dataSet.get(position);
    }
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
            viewHolder.txtcDireccion = (TextView) convertView.findViewById(R.id.txtcDireccion);
            result=convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        DataModel item = getItem(position);
        viewHolder.txtcDireccion.setText(item.cDireccion);
        return result;
    }
}