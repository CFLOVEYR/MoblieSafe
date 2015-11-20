package com.tofirst.mobilesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.tofirst.mobilesafe.db.dao.BlackNumberDao;

import java.util.Random;

/**
 * Created by StudyLifetime on 2015/11/20.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    public Context mContext;

    @Override
    protected void setUp() throws Exception {
        this.mContext=getContext();
        super.setUp();
    }
    public  void testAdd(){
        BlackNumberDao dao=new BlackNumberDao(mContext);
        Random r=new Random();
        for (int i = 0; i <200; i++) {
            dao.insert("138000000"+i, String.valueOf(r.nextInt(3)+1));
        }
    }
    public void testDelete(){
        BlackNumberDao dao=new BlackNumberDao(mContext);
        for (int i = 0; i <50 ; i++) {
            dao.delete("138000000" + i);
        }
    }
    public  void testUpdate(){
        BlackNumberDao dao= new BlackNumberDao(mContext);
        dao.update("138000000"+51,"1");
    }
    public void testqueryMode(){
        BlackNumberDao dao= new BlackNumberDao(mContext);
        String mode=dao.query("138000000" + 100);
    }
    public void testqueryExist(){
        BlackNumberDao dao= new BlackNumberDao(mContext);
        boolean result=dao.queryExist("138000000" + 100);
    }
    public void testqueryAll(){
        BlackNumberDao dao= new BlackNumberDao(mContext);
        dao.queryAll();
    }



}
