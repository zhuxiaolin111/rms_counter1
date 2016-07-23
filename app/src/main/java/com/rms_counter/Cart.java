package com.rms_counter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

public class Cart {
	private Db db = new Db();
	
	public Cart(Context context){
		db.openDb(context);	
	}
	
	public String loadCartData(String code) {
		String jsonresult = "";
		String cartContent = "";
		db.beginTransaction();
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS shopping_cart (id INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER, product _num VARCHAR, attr_code VARCHAR)");
			if(code.equals("")==false){
				Cursor co = db.rawQuery(
						"SELECT * FROM shopping_cart where attr_code='" + code
								+ "'", null);
				if(co.getCount()>0){				
					String updateSql = "update shopping_cart set product_num=(product_num+1) where attr_code='" + code +"'";
					db.execSQL(updateSql);
				}else{					
					Cursor cp = db.rawQuery(
							"SELECT * FROM products where attr_code='" + code
									+ "'", null);
					while (cp.moveToNext()) {
						int product_id = cp.getInt(cp.getColumnIndex("product_id"));
						String num = "INSERT INTO shopping_cart(product_id, product_num,attr_code) VALUES ("+ product_id +", 1, '"+ code +"')";
						db.execSQL(num);
					}
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		
		Cursor c = db.rawQuery("SELECT * FROM shopping_cart", null);
		if (c.getCount() > 0) {
			Cursor cc = db
					.rawQuery(
							"SELECT * FROM shopping_cart, products where shopping_cart.product_id=products.product_id and shopping_cart.attr_code=products.attr_code",
							null);
			JSONObject object = new JSONObject();
			JSONArray ja = new JSONArray();
			JSONObject jo = null;
			double sum = 0;
			while (cc.moveToNext()) {
				jo = new JSONObject();
				String attrName = cc.getString(cc.getColumnIndex("attr_name"));
				String productName = cc.getString(cc.getColumnIndex("product_name"));
				double attrPrice = cc.getDouble(cc.getColumnIndex("attr_price"));
				int productNum = cc.getInt(cc.getColumnIndex("product_num"));
				int id = cc.getInt(cc.getColumnIndex("id"));
				sum = sum + attrPrice*productNum;
				try {
					jo.put("attr_name", attrName);
					jo.put("product_name", productName);
					jo.put("attr_price", attrPrice);
					jo.put("product_num", productNum);
					jo.put("id", id);
					ja.put(jo);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				object.put("cartContent", ja);
				object.put("totalPrice", sum);
				object.put("result", "ok");
				jsonresult = object.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				try {
					object.put("result", "err");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				
			}
			
		}
		return jsonresult;
	}
	
	
}
