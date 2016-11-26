package pow.unionbankph.com.pow;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import pow.unionbankph.com.pow.R;

public class PaymentOptionsArrayAdapter extends ArrayAdapter<PaymentOption> {
    private final Context context;
    private final ArrayList<PaymentOption> values;
    public PaymentOptionsArrayAdapter(Context context, ArrayList<PaymentOption> list) {
        super(context, R.layout.rowlayout_paymentoptions, list);
        this.context = context;
        this.values = list;

    }

    static class ViewHolder {
        protected ImageView icon;
        protected TextView label;
        protected RadioButton radioButton;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout_paymentoptions, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.icon);
            viewHolder.label = (TextView) rowView.findViewById(R.id.label);
            viewHolder.radioButton = (RadioButton) rowView.findViewById(R.id.radioButton);
            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.label.setText("**** **** **** " + right(values.get(position).cardnumber,4));
        if(values.get(position).selected.equals("1")){
            holder.radioButton.setChecked(true);
        }else{
            holder.radioButton.setChecked(false);
        }
        holder.label.setSelected(false);

        try{
            //if(context.getResources().getIdentifier(values.get(position).imguri, "drawable", context.getPackageName()) == 0)
            //    holder.icon.setImageResource(R.drawable.card_classic_paywave);
            //else
            //    holder.icon.setImageResource(context.getResources().getIdentifier(values.get(position).imguri, "drawable", context.getPackageName()));
        }catch(Exception e){
            //holder.icon.setImageResource(R.drawable.card_classic_paywave);
        }


        return rowView;
    }
    public static String right(String value, int length) {
        // To get right characters from a string, change the begin index.
        return value.substring(value.length() - length);
    }
}
