package com.rms_counter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Product {
	private Db db = new Db();	
	private static JSONObject json;
	private static JSONArray jsonProduct;
	private static int jsonErr = 1;
	
	public Product(Context context){
		db.openDb(context);;		
	}
	
	public void downloadProductData(String token, String appLang) {		
		String url = UrlAddress.url + "api/json_product_download.php?token="+ token +"&appLang="+ appLang;
		String res = getServerJsonDataWithNoType(url);
		String sqlStr = "";
		try {
			json = new JSONObject(res);
			jsonProduct = json.getJSONArray("product");
			jsonErr = json.getInt("err");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			jsonErr = 1;
			e1.printStackTrace();
		}
		db.beginTransaction();
		try {
			db.execSQL("DROP TABLE IF EXISTS products");
			db.execSQL("CREATE TABLE products (id INTEGER,product_id INTEGER,attr_code VARCHAR,attr_name VARCHAR,attr_price decimal(9,2),attr_num INTEGER,product_name VARCHAR)");
			if (jsonErr == 0) {
				int productNum = jsonProduct.length();
				for (int i = 0; i < productNum; i++) {
					String _id = jsonProduct.getJSONObject(i).getString(
							"id");
					int id = Integer.parseInt(_id);
					String product_id = jsonProduct.getJSONObject(i)
							.getString("product_id");
					String attr_code = jsonProduct.getJSONObject(i)
							.getString("attr_code");
					String attr_name = jsonProduct.getJSONObject(i)
							.getString("attr_name");
					String attr_price = jsonProduct.getJSONObject(i)
							.getString("attr_price");
					String _attr_num = jsonProduct.getJSONObject(i)
							.getString("attr_num");
					String product_name = jsonProduct.getJSONObject(i)
							.getString("product_name");
					int attr_num = Integer.parseInt(_attr_num);
					sqlStr = sqlStr + "(" + id + "," + product_id + ",'"
							+ attr_code + "','" + attr_name + "',"
							+ attr_price + "," + attr_num + ",'"
							+ product_name + "'),";
				}
				sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
				String insertSql = "INSERT INTO products(id,product_id, attr_code,attr_name, attr_price, attr_num,product_name) VALUES "
						+ sqlStr;
				db.execSQL(insertSql);
			}
			db.setTransactionSuccessful();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.endTransaction();		
			
		}
	}
	
	private String getServerJsonDataWithNoType(String downUrl) {
		String res = HttpUtils.getJsonContent(downUrl);
		return res;
	}
}
