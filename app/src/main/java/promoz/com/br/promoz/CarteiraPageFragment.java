package promoz.com.br.promoz;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.List;
import promoz.com.br.promoz.adapter.CouponAdapter;
import promoz.com.br.promoz.adapter.HistoricAdapter;
import promoz.com.br.promoz.dao.CouponDAO;
import promoz.com.br.promoz.dao.HistoricCoinDAO;
import promoz.com.br.promoz.dao.WalletDAO;
import promoz.com.br.promoz.dao.db.PromozContract;
import promoz.com.br.promoz.model.Coupon;
import promoz.com.br.promoz.model.HistoricCoin;

public class CarteiraPageFragment extends Fragment{

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private int walletID=0;
    private WalletDAO walletDAO;
    private CouponDAO couponDAO;
    private HistoricCoinDAO historicCoinDAO;
    private List<Coupon> couponList;
    private List<HistoricCoin> historicCoinList;
    private CouponAdapter couponAdapter;
    private HistoricAdapter historicAdapter;
    public ListView listcoupon;
    public ListView listhistoric;
    public static Handler handler;
    OnGetUserID callback;

    public interface OnGetUserID {
        public Integer getUserId();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (OnGetUserID) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public static CarteiraPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        CarteiraPageFragment fragment = new CarteiraPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateCouponList(){
        couponList = couponDAO.listById(walletID, PromozContract.Coupon.COLUMN_CPN_IND_VALID + " DESC, " + PromozContract.Coupon.COLUMN_CPN_DT_EXP + " ASC, " + PromozContract.Coupon.COLUMN_CPN_DT_USE + " DESC");
        couponAdapter = new CouponAdapter(getContext(),couponList);
        listcoupon.setAdapter(couponAdapter);
    }

    private void setHistoricDate(Integer date){
        historicCoinList = historicCoinDAO.listByDate(walletID,date);

        historicAdapter = new HistoricAdapter(getContext(),historicCoinList);
        listhistoric.setAdapter(historicAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        if(historicCoinDAO == null)
            historicCoinDAO = new HistoricCoinDAO(getContext());

        if(couponDAO == null)
            couponDAO = new CouponDAO(getContext());

        if(walletDAO == null)
            walletDAO = new WalletDAO(getContext());

        walletID = walletDAO.walletIdByUserId(callback.getUserId());

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                if( msg.what == 100 )
                    updateCouponList();
                    //couponAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        //Caso Tab SALDO
        if (mPage == 1) {
            //TODO login
            view = inflater.inflate(R.layout.saldo_layout, container, false);
            TextView textoSaldo = (TextView) view.findViewById(R.id.saldoCarteira);
            textoSaldo.setText(walletDAO.walletById(walletID).getAmountCoin().toString());
            listhistoric = (ListView) view.findViewById(R.id.historic_list);
            listhistoric.setDividerHeight(0);
            RadioButton btn7D = (RadioButton) view.findViewById(R.id.seteDias);
            btn7D.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setHistoricDate(-7);
                }
            });

            RadioButton btn15D = (RadioButton) view.findViewById(R.id.quinzeDias);
            btn15D.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setHistoricDate(-15);
                }
            });

            RadioButton btn30D = (RadioButton) view.findViewById(R.id.trintaDias);
            btn30D.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setHistoricDate(-30);
                }
            });

            setHistoricDate(-7);
            return view;
        } else {
            //Caso Tab CUPOM
            view = inflater.inflate(R.layout.cupom_layout, container, false);
            listcoupon = (ListView) view.findViewById(R.id.lstCoupon);
            listcoupon.setDividerHeight(5);
            updateCouponList();
            return view;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(historicCoinDAO != null)
            historicCoinDAO.closeDataBase();

        if(couponDAO != null)
            couponDAO.closeDataBase();

        if(walletDAO != null)
            walletDAO.closeDataBase();
    }
}