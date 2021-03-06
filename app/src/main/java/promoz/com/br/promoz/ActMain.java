package promoz.com.br.promoz;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import promoz.com.br.promoz.dao.CouponDAO;
import promoz.com.br.promoz.dao.HistoricCoinDAO;
import promoz.com.br.promoz.dao.UserDAO;
import promoz.com.br.promoz.dao.WalletDAO;
import promoz.com.br.promoz.model.Coupon;
import promoz.com.br.promoz.model.HistoricCoin;
import promoz.com.br.promoz.model.User;
import promoz.com.br.promoz.util.DateUtil;

public class ActMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Integer userID=0;
    private ImageView foto,fotoclick;
    int backButtonCount = 0;
    int countDown = 10000;
    final Context context =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backButtonCount = 0;
        checkLogged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        foto =  (ImageView) hView.findViewById(R.id.foto_nav);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PerfilActivity.class);
                closeMenu();
                context.startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActMain.this,CarteiraActivity.class);
                intent.putExtra(User.getChave_ID(),userID);
                ActMain.this.startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                backButtonCount =0;
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View drawerView) {
                backButtonCount =0;
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setMenu();
    }

    public void onMoeda(View v){
        addCoin(1);
        String texto = "Ganhou uma moeda";
        Snackbar.make(findViewById(R.id.drawer_layout),texto, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void onBau(View v){
         addCupom();
    }

    // TODO: Adicionar Cupom apenas para protótipo
    public void addCupom(){
        WalletDAO wallet = new WalletDAO(this);
        Integer walletId = wallet.walletIdByUserId(userID);
        Integer walletAmount = wallet.getAmountByWalletId(walletId);

        if(walletAmount >= 2) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 10);
            String date = new SimpleDateFormat(DateUtil.YYYYMMDD_HHmmss).format(new Date(cal.getTimeInMillis()));

            Coupon coupon = new Coupon();
            coupon.setTitle("Mês do Fitness");
            coupon.setSubTitle("R$50,00 de desconto em compras acima de R$700,00");
            coupon.setInfo("Neste mês de Fevereiro, a Centauro traz para você promoções imperdíveis: Toda linha fitness com até 50% de desconto.");
            coupon.setDateExp(date);
            coupon.setPrice(2);
            coupon.setValid(1);
            coupon.setStoreId(2);
            coupon.setImg(R.drawable.cia_logo);
            coupon.setWalletId(walletId);

            CouponDAO couponDAO = new CouponDAO(this);
            couponDAO.save(coupon);
            couponDAO.closeDataBase();

            String texto = "Comprou um cupom";
            Snackbar.make(findViewById(R.id.drawer_layout),texto, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();

        }else{
            Snackbar.make(findViewById(R.id.drawer_layout),getResources().getString(R.string.saldoInsuficiente) + ": " + walletAmount, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (closeMenu()) {
            if (backButtonCount >= 1) {
                moveTaskToBack(true);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Pressione o botão voltar novamente para sair do aplicativo", Toast.LENGTH_SHORT).show();
                backButtonCount++;
                new CountDownTimer(countDown, 5000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        backButtonCount = 0;
                    }
                }.start();
            }
        }
    }


    private void checkLogged(){
        userID = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE).getInt(User.getChave_ID(),0);
        if(userID == 0)
            finish();
    }

    private void setMenu(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        UserDAO userDAO = new UserDAO(this);
        User user = userDAO.userById(userID);

        if(user != null) {
            byte[] bitmapdata;
            bitmapdata = user.getImg();
            TextView name = (TextView) hView.findViewById(R.id.navDrawerNome);
            name.setText(user.getNome());
            if(bitmapdata != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                if (bitmap != null){
                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    drawable.setCircular(true);
                    foto.setImageDrawable(drawable);
                }
            } else {
                Resources res = getResources();
                Bitmap src = BitmapFactory.decodeResource(res, R.drawable.default_photo);
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), src);
                drawable.setCircular(true);
                foto.setImageDrawable(drawable);
            }
        }
        userDAO.closeDataBase();
    }

    private boolean closeMenu(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            backButtonCount =0;
            drawer.closeDrawer(GravityCompat.START); // recolhe o menu caso esteja "aberto"
            return false;
        }
        return true;
    }

    @Override
    protected void onRestart() {
        checkLogged();
        super.onRestart();
        setMenu();
    }

    // TODO: Adicionar moedas apenas para protótipo
    private void addCoin(Integer amountCoin) {
        WalletDAO wallet = new WalletDAO(this);
        Integer walletId = wallet.walletIdByUserId(userID);
        String date = new SimpleDateFormat(DateUtil.YYYYMMDD_HHmmss).format(new Date());
        HistoricCoin historicCoin = new HistoricCoin(walletId,1,date,amountCoin,0);
        HistoricCoinDAO historicCoinDAO = new HistoricCoinDAO(this);
        historicCoinDAO.save(historicCoin);
        wallet.closeDataBase();
        historicCoinDAO.closeDataBase();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        backButtonCount = 0;
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            Intent intent = new Intent(this,PerfilActivity.class);
            intent.putExtra(User.getChave_ID(),userID);
            this.startActivity(intent);

        } else if (id == R.id.nav_wallet) {
            Intent intent = new Intent(this,CarteiraActivity.class);
            intent.putExtra(User.getChave_ID(),userID);
            this.startActivity(intent);
        } else if (id == R.id.nav_missions) {
            Context contexto = getApplicationContext();
            String texto = "MISSÔES";
            int duracao = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(contexto, texto,duracao);
            toast.show();

        } else if (id == R.id.nav_shop) {
            Intent intent = new Intent(this,LojaActivity.class);
            intent.putExtra(User.getChave_ID(), userID);
            this.startActivity(intent);

        } else if (id == R.id.nav_config) {
            Context contexto = getApplicationContext();
            String texto = "CONFIGURAÇÕES";
            int duracao = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(contexto, texto,duracao);
            toast.show();

        } else if (id == R.id.nav_help) {
            Context contexto = getApplicationContext();
            String texto = "AJUDA";
            int duracao = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(contexto, texto,duracao);
            toast.show();

        } else if (id == R.id.nav_feedback) {
            Context contexto = getApplicationContext();
            String texto = "FEEDBACK";
            int duracao = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(contexto, texto,duracao);
            toast.show();

        } else if (id == R.id.nav_terms) {
            Context contexto = getApplicationContext();
            String texto = "TERMO DE SERVOÇO";
            int duracao = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(contexto, texto,duracao);
            toast.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
