package pow.unionbankph.com.pow;

import java.util.HashMap;

public class PaymentOption {
    public String cardtype;
    public String cardnumber;
    public String isUBP;
    public String acctno;
    public String selected;
    public PaymentOption(){
        super();
    }

    public PaymentOption(HashMap<String, String> map) {
        super();

        this.cardtype = map.get("cardtype");
        this.cardnumber = map.get("cardnumber");
        this.selected = map.get("selected");
        this.isUBP = map.get("isUBP");
        this.acctno = map.get("acctno");
    }
}
