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

import java.text.DecimalFormat;

import pow.unionbankph.com.pow.R;

public class PurchasePaymentsArrayAdapter extends ArrayAdapter<PurchasePayment> {
    private final Context context;
    private final ArrayList<PurchasePayment> values;
    public PurchasePaymentsArrayAdapter(Context context, ArrayList<PurchasePayment> list) {
        super(context, R.layout.rowlayout_purchasepayments, list);
        this.context = context;
        this.values = list;

    }

    static class ViewHolder {
        protected ImageView icon;
        protected TextView label;
        protected TextView category;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout_purchasepayments, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.icon);
            viewHolder.label = (TextView) rowView.findViewById(R.id.label);
            viewHolder.category = (TextView) rowView.findViewById(R.id.category);
            rowView.setTag(viewHolder);
        }

        // fill data
        DecimalFormat numberFormat = new DecimalFormat("#,###.00");

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.label.setText(values.get(position).dte + " - " + "PHP " + numberFormat.format(Double.parseDouble(values.get(position).amt)));
        holder.category.setText(values.get(position).mName + " " + "**** " + right(values.get(position).sacc,4));

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
