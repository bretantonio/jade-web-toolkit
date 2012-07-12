package suncertify;

import java.io.IOException;

import suncertify.db.Data;
import suncertify.db.DataInfo;
import suncertify.db.DatabaseException;
import suncertify.db.FieldInfo;

public class Test {

	/**
	 * @param args
	 * @throws IOException
	 * @throws DatabaseException 
	 */
	public static void main(String[] args) throws IOException, DatabaseException {

		String dbname = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "db.db";
		System.out.println(dbname);
		Data d = new Data(dbname);

		FieldInfo[] f = d.getFieldInfo();

		System.out.println(f);

		for (int i = 0; i < f.length; i++) {
			System.out.println(f[i].getName() + "\t" + f[i].getLength());
		}

		System.out.println(d.getRecordCount());
		
		for (int j= 0;j < 24; j++) {
			DataInfo di = d.getRecord(j+1);

			f = di.getFields();
			if(j==0){
				for(int k=0;k<f.length;k++){
					System.out.print(f[k].getName() +"\t");
				}
				System.out.println("");
			}
			String [] s = di.getValues();
			for(int m=0;m<s.length;m++){
				System.out.print(s[m] +"\t");
			}
			System.out.println("");
		}
		
	}

}
