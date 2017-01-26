package promoz.com.br.promoz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import promoz.com.br.promoz.dao.db.MySQLiteDatabase;
import promoz.com.br.promoz.dao.db.PromozContract;
import promoz.com.br.promoz.model.HistoricCoin;

/**
 * Created by vallux on 26/01/17.
 */

public class HistoricCoinDAO extends PromozContract.HistoricCoin {

    private MySQLiteDatabase myDatabaseHelper;
    private SQLiteDatabase database;

    public HistoricCoinDAO(Context context) {
        this.myDatabaseHelper = new MySQLiteDatabase(context);
    }

    private SQLiteDatabase getDatabase(){
        if (database == null){
            database = myDatabaseHelper.getWritableDatabase();
        }
        return database;
    }

    private HistoricCoin populate(Cursor cursor){ // Popula o objeto com os dados do cursor
        HistoricCoin model = new HistoricCoin(
                cursor.getInt(cursor.getColumnIndex(_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_WALLET_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_HST_TP_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_HST_DT_OPER)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_AMOUNT_COIN))
        );
        return model;
    }

    public long save(HistoricCoin historic){ // salva os campos do objeto na tabela - atualiza ou cria um novo caso não exista

        ContentValues values = new ContentValues();
        values.put(COLUMN_WALLET_ID, historic.getWalletId());
        values.put(COLUMN_HST_TP_ID, historic.getHistoricTypeId());
        values.put(COLUMN_HST_DT_OPER, historic.getHistoricDateOperation());
        values.put(COLUMN_AMOUNT_COIN, historic.getAmountCoin());

        if(historic.get_id() != null){
            return getDatabase().update(TABLE_NAME, values, "_id = ?", new String[]{ historic.get_id().toString() });
        }
        return getDatabase().insert(TABLE_NAME, null, values);
    }

    public List<HistoricCoin> list(){
        Cursor cursor = getDatabase().query(TABLE_NAME, allFields, null, null, null, null, null);

//        cursor.moveToFirst();
        List<HistoricCoin> lst = new ArrayList<HistoricCoin>();
        while (cursor.moveToNext())
            lst.add(populate(cursor));
        cursor.close();
        return lst;
    }

    public boolean remove(int id){
        return getDatabase().delete(TABLE_NAME, "_id = ?", new String[]{ Integer.toString(id) }) > 0;
    }
}