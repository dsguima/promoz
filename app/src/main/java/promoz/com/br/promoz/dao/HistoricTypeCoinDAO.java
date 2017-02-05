package promoz.com.br.promoz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import promoz.com.br.promoz.dao.db.AppDatabase;
import promoz.com.br.promoz.dao.db.PromozContract;
import promoz.com.br.promoz.model.HistoricTypeCoin;

/**
 * Created by vallux on 25/01/17.
 */

public class HistoricTypeCoinDAO extends PromozContract.HistoricTypeCoin {

    private AppDatabase dbHelper;
    private SQLiteDatabase database;

    public HistoricTypeCoinDAO(Context context) {
        dbHelper = new AppDatabase(context);
        database = dbHelper.getDatabase();
    }

    private HistoricTypeCoin populate(Cursor cursor){ // Popula o objeto "HistoricTypeCoin" com os dados do cursor
        HistoricTypeCoin model = new HistoricTypeCoin(
                cursor.getInt(cursor.getColumnIndex(_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_HST_TP_DESC))
        );
        return model;
    }

    public long save(HistoricTypeCoin hist){ // salva os campos do objeto na tabela - atualiza ou cria um novo caso não exista

        ContentValues values = new ContentValues();
        values.put(COLUMN_HST_TP_DESC, hist.getDescription());

        if(hist.get_id() != null){
            return database.update(TABLE_NAME, values, "_id = ?", new String[]{ hist.get_id().toString() });
        }
        return database.insert(TABLE_NAME, null, values);
    }

    public List<HistoricTypeCoin> list(){
        Cursor cursor = database.query(TABLE_NAME, allFields, null, null, null, null, null);

//        cursor.moveToFirst();
        List<HistoricTypeCoin> lst = new ArrayList<HistoricTypeCoin>();
        while (cursor.moveToNext())
            lst.add(populate(cursor));
        cursor.close();
        return lst;
    }

    public void closeDatabase(){
        database.close();
    }

    public boolean remove(int id){
        return database.delete(TABLE_NAME, "_id = ?", new String[]{ Integer.toString(id) }) > 0;
    }
}
