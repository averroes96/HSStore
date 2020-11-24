package com.averroes.hsstock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.averroes.hsstock.models.Depot;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.models.Sell;

public class DBHandler extends SQLiteOpenHelper {

    private Context context;
    private static final int DB_VERSION = 7;
    private static String DB_NAME = "Product.db";
    public static final String T_PRODUCT = "product";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_SOLD = "sold";
    public static final String T_SELL = "sell";
    public static final String COLUMN_SELL_ID = "id";
    public static final String COLUMN_PRODUCT_ID = "prod_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PRICE = "size";
    public static final String COLUMN_IMAGE = "image";
    public static final String T_DEPOT = "depot";
    public static final String COLUMN_DEPOT_ID = "id";
    public static final String COLUMN_REF = "reference";
    public static final String COLUMN_LOCATION = "location";


    public DBHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query = "CREATE TABLE " + T_PRODUCT
                + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_SIZE + " INTEGER, "
                + COLUMN_COLOR + " TEXT"
                + " );" ;
        sqLiteDatabase.execSQL(query);

        query = "CREATE TABLE " + T_SELL
                + " ( "
                + COLUMN_SELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " DATETIME, "
                + COLUMN_PRICE + " INTEGER, "
                + COLUMN_PRODUCT_ID + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + T_PRODUCT + "(" + COLUMN_ID + ")"
                + " );" ;

        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        switch (i1){
            case 1:
                onCreate(sqLiteDatabase);
            case 2:
                sqLiteDatabase.execSQL("ALTER TABLE " + T_PRODUCT + " ADD COLUMN " + COLUMN_SOLD + " INTEGER DEFAULT 0");
                break;
            case 3:
                sqLiteDatabase.execSQL("DROP TABLE " + T_SELL);
                String query = "CREATE TABLE " + T_SELL
                        + " ( "
                        + COLUMN_SELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        + COLUMN_PRICE + " INTEGER, "
                        + COLUMN_PRODUCT_ID + " INTEGER, "
                        + "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + T_PRODUCT + "(" + COLUMN_ID + ")"
                        + " );" ;

                sqLiteDatabase.execSQL(query);
                break;
            case 4:
                sqLiteDatabase.execSQL("ALTER TABLE " + T_PRODUCT + " ADD COLUMN " + COLUMN_IMAGE + " BLOB");
                break;

            case 5:
                sqLiteDatabase.execSQL("DROP TABLE " + T_PRODUCT);
                query = "CREATE TABLE " + T_PRODUCT
                        + " ( "
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " TEXT, "
                        + COLUMN_SIZE + " INTEGER, "
                        + COLUMN_COLOR + " TEXT, "
                        + COLUMN_IMAGE + " TEXT"
                        + " );" ;
                sqLiteDatabase.execSQL(query);
                break;

            case 6:
                sqLiteDatabase.execSQL("ALTER TABLE " + T_PRODUCT + " ADD COLUMN " + COLUMN_SOLD + " INTEGER DEFAULT 0");
                break;

            case 7:
                query = "CREATE TABLE " + T_DEPOT
                        + " ( "
                        + COLUMN_DEPOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_REF + " TEXT, "
                        + COLUMN_LOCATION + " INTEGER"
                        + " );" ;
                sqLiteDatabase.execSQL(query);
                break;

        }

    }

    public void addProduct(Product product){

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.get_name());
        values.put(COLUMN_SIZE, product.get_size());
        values.put(COLUMN_COLOR, product.get_color());
        values.put(COLUMN_IMAGE, product.get_image());
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.insert(T_PRODUCT, null, values);

        db.close();

        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de l\'ajout du produit", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Product added", Toast.LENGTH_LONG).show();

    }

    public void addSell(Sell sell){

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRICE, sell.get_price());
        values.put(COLUMN_PRODUCT_ID, sell.getProduct().get_id());
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.insert(T_SELL, null, values);

        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de l\'ajout d'achat", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Sell added", Toast.LENGTH_LONG).show();

    }

    public Product getProduct(Product product){

        String query = "SELECT * FROM " + T_PRODUCT
                        + " WHERE name = '" + product.get_name()
                        + "' AND size = '" + product.get_size()
                        + "' AND color = '" + product.get_color()
                        + "' AND sold = 0 LIMIT 1";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor;

        if(sqLiteDatabase != null){
            cursor = sqLiteDatabase.rawQuery(query, null);
            if(cursor.moveToNext()){
                product.set_id(cursor.getInt(0));
                return product;
            }
            return null;
        }

        return null;

    }

    public void deleteProduct(String id){

        SQLiteDatabase db = getWritableDatabase();
        long res = db.delete(T_PRODUCT, "id = ?", new String[]{id});
        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de la suppression du produit", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Product deleted", Toast.LENGTH_LONG).show();

    }

    public void deleteSell(Sell sell){

        SQLiteDatabase db = getWritableDatabase();

        long res = db.delete(T_SELL, "id = ?", new String[]{String.valueOf(sell.get_id())});
        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de suppression d'achat", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Vend deleted", Toast.LENGTH_LONG).show();

    }


    public void deleteSells(){

        SQLiteDatabase db = getWritableDatabase();
        long res = db.delete(T_SELL, null, null);
        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de suppression des achats", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Sells all deleted", Toast.LENGTH_LONG).show();

    }

    public Cursor readAllData(){

        String query = "SELECT id,name,size,color,image FROM " + T_PRODUCT + " WHERE " + COLUMN_SOLD + " = ? ORDER BY " + COLUMN_NAME ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if(sqLiteDatabase != null){
            cursor = sqLiteDatabase.rawQuery(query, new String[]{"0"});
        }

        return cursor;

    }

    public void updateProduct(Product product){

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.get_name());
        values.put(COLUMN_SIZE, product.get_size());
        values.put(COLUMN_COLOR, product.get_color());
        values.put(COLUMN_SOLD, product.get_sold());
        values.put(COLUMN_IMAGE, product.get_image());
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.update(T_PRODUCT, values, "id = ?", new String[]{String.valueOf(product.get_id())});

        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de l\'ajout du produit", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Product updated", Toast.LENGTH_LONG).show();

    }

    public void updateSold(int id, int sold){

        ContentValues values = new ContentValues();
        values.put(COLUMN_SOLD, sold);
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.update(T_PRODUCT, values, "id = ?", new String[]{String.valueOf(id)});

        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de l\'ajout d'achat", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Product update", Toast.LENGTH_LONG).show();

    }

    public Cursor getAllSells() {

        String query = "SELECT sell.id, sell.size, time(sell.date), product.id, product.name, product.size, product.color FROM " + T_SELL + " INNER JOIN " + T_PRODUCT + " ON sell.prod_id = product.id WHERE date(sell.date) = date('now') ORDER BY " + COLUMN_DATE ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if(sqLiteDatabase != null){
            cursor = sqLiteDatabase.rawQuery(query, null);
        }

        return cursor;
    }

    public void updateSell(Sell sell) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRICE, sell.get_price());
        values.put(COLUMN_PRODUCT_ID, sell.getProduct().get_id());

        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.update(T_SELL, values, "id = ?", new String[]{String.valueOf(sell.get_id())});

        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de l\'ajout du position", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Sell updated", Toast.LENGTH_LONG).show();
    }

    public Cursor getAllDepots() {
        String query = "SELECT id, reference, location FROM " + T_DEPOT + " ORDER BY " + COLUMN_LOCATION ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if(sqLiteDatabase != null){
            cursor = sqLiteDatabase.rawQuery(query, null);
        }

        return cursor;
    }

    public long addDepot(Depot depot){

        ContentValues values = new ContentValues();
        values.put(COLUMN_REF, depot.get_reference());
        values.put(COLUMN_LOCATION, depot.get_location());
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.insert(T_DEPOT, null, values);

        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de l\'ajout du position", Toast.LENGTH_LONG).show();

        return res;

    }

    public boolean updateDepot(Depot depot) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_REF, depot.get_reference());
        values.put(COLUMN_LOCATION, depot.get_location());

        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.update(T_DEPOT, values, "id = ?", new String[]{String.valueOf(depot.get_id())});

        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de l\'ajout du position", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Position updated", Toast.LENGTH_LONG).show();

        return  res != -1;

    }

    public void deleteDepot(Depot depot){

        SQLiteDatabase db = getWritableDatabase();

        long res = db.delete(T_DEPOT, "id = ?", new String[]{String.valueOf(depot.get_id())});
        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de suppression d'achat", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Depot deleted", Toast.LENGTH_LONG).show();

    }

    public void removeDepot(Depot depot){

        SQLiteDatabase db = getWritableDatabase();

        long res = db.delete(T_DEPOT, "reference = ? AND location = ?", new String[]{depot.get_reference(), depot.get_location()});
        if(res == -1)
            Toast.makeText(context, "Erreur de base de données lors de suppression d'achat", Toast.LENGTH_LONG).show();

    }

    public Cursor getAllPositions() {
        String query = "SELECT distinct location FROM " + T_DEPOT ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if(sqLiteDatabase != null){
            cursor = sqLiteDatabase.rawQuery(query, null);
        }

        return cursor;
    }

    public Cursor getPositionRefs(String name) {
        String query = "SELECT reference FROM " + T_DEPOT + " WHERE location = ?" ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if(sqLiteDatabase != null){
            cursor = sqLiteDatabase.rawQuery(query, new String[]{name});
        }

        return cursor;
    }
}
