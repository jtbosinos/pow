package pow.unionbankph.com.pow;

import java.util.HashMap;

public class PurchasePayment {
    public String tid;
    public String sacc;
    public String tacc;
    public String amt;
    public String mName;
    public String dte;
    public PurchasePayment(){
        super();
    }

    public PurchasePayment(HashMap<String, String> map) {
        super();

        this.tid = map.get("tid");
        this.sacc = map.get("sacc");
        this.tacc = map.get("tacc");
        this.amt = map.get("amt");
        this.mName = map.get("mName");
        this.dte = map.get("dte");
    }
}
