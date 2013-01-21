package org.qrone.r7.test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class MongoTest {

	public static void main(String[] args) {
		Mongo m;
		try {
			m = new Mongo();
			DB db = m.getDB("test" );

			DBCollection coll = db.getCollection("things");
			DBCursor cur = coll.find();

			while(cur.hasNext()) {
	            System.out.println(cur.next());
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
