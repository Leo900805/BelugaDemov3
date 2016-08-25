package com.hosengamers.beluga.loginpage.datacontrol;

import android.content.Context;

import com.hosengamers.beluga.R;


/**
 * Created by deskuser on 2015/10/6.
 */
public class UsedString {
   
    public static String getLoginstring(Context context, int id){
        String LoginReturn = "";
        switch(id){
            case -1:
                LoginReturn = context.getResources().getString(R.string.Wrong_AppID_Type);
                break;
            case -2:
                LoginReturn = context.getResources().getString(R.string.Ac_Or_Pwd_Length_Err_Type);
                break;
            case -4:
                LoginReturn = context.getResources().getString(R.string.Ac_Or_Pwd_Err_Type);
                break;
            case -10:
                LoginReturn = context.getResources().getString(R.string.Ac_Has_Been_Baned_Type);
                break;
            case -11:
                LoginReturn = context.getResources().getString(R.string.Ac_Has_Been_Disabled_Type);
                break;
            case 1:
                LoginReturn = context.getResources().getString(R.string.Success_Login_Type);
                break;
            case -97:
                LoginReturn = context.getResources().getString(R.string.Checksum_Err_Type);
                break;
            case -98:
                LoginReturn = context.getResources().getString(R.string.System_Err_Type);
                break;
            case -99:
                LoginReturn = context.getResources().getString(R.string.Program_Err_Type);

                break;
        }
        return LoginReturn;
    }
    
    public static String getThirdPartnerLoginStatusString(Context context, int id){
        String str = "";
        switch(id){
            case 1:
            	str = context.getResources().getString(R.string.Third_Partner_Login_Success);
              break;
        }
        return str;
    }
    public static String getFastRegistrationGenerateString(Context context, int id){
        String FastRegisGenString = "";
        switch(id){
            case -1:
                FastRegisGenString = context.getResources().getString(R.string.Wrong_AppID_Type);
                
                break;
            case -2:
                FastRegisGenString =  context.getResources().getString(R.string.Write_DB_failed_Type);
                
                break;
            case -97:
                FastRegisGenString = context.getResources().getString(R.string.Checksum_Err_Type);
                
                break;
            case -98://
                FastRegisGenString = context.getResources().getString(R.string.System_Err_Type);
                
                break;
            case -99://
                FastRegisGenString = context.getResources().getString(R.string.Program_Err_Type);
                
                break;
            case 1:
                FastRegisGenString = context.getResources().getString(R.string.Success_Quick_Reg_Type);
                
                break;

        }
        return FastRegisGenString;
    }
    public static String getFastRegistrationString(Context context, int id){
        String FastRegisString = "";
        switch(id){
            case 1:
                FastRegisString = context.getResources().getString(R.string.Success_Quick_Reg_Type);
                
                break;
            case -1:
                
                FastRegisString = context.getResources().getString(R.string.Wrong_AppID_Type);
                break;
            case -2:
                FastRegisString = context.getResources().getString(R.string.Pwd_Length_Err_Type);
                
                break;
            case -97:
                
                FastRegisString = context.getResources().getString(R.string.Checksum_Err_Type);
                break;
            case -98:
                
                FastRegisString = context.getResources().getString(R.string.System_Err_Type);
                break;
            case -99://
                
                FastRegisString = context.getResources().getString(R.string.Program_Err_Type);
                break;

        }
        return FastRegisString;
    }
    public static String getRegisterString(Context context, int id){
        String RegisString = "";
        switch (id) {
            case -1:
                RegisString = context.getResources().getString(R.string.Wrong_AppID_Type);
                
                break;
            case -2:
                RegisString = context.getResources().getString(R.string.Ac_Or_Pwd_Length_Err_Type);
                
                break;
            case -3:
                RegisString = context.getResources().getString(R.string.Pwd_Length_Err_Type);
                
                break;
            case -4:
                RegisString = context.getResources().getString(R.string.Ac_Has_Been_Used_Type);
                
                break;
            case -6://
                RegisString = context.getResources().getString(R.string.Ac_With_Illegal_Char_Type);

                break;
            case -7://
                RegisString = context.getResources().getString(R.string.Pwd_With_Illegal_Char_Type);

                break;
            case 1:
                RegisString = context.getResources().getString(R.string.Success_Register_Type);

                break;
            case -97://
                RegisString = context.getResources().getString(R.string.Checksum_Err_Type);

                break;
            case -98://
                RegisString = context.getResources().getString(R.string.System_Err_Type);

                break;
            case -99://
                RegisString = context.getResources().getString(R.string.Program_Err_Type);

                break;
            case -102:
                RegisString = context.getResources().getString(R.string.Data_Analysis_Err_Type);

                break;
        }

        return RegisString;
    }
    public static String getChangePasswordString(Context context, int id){
        String Changepwdstr = "";
        switch(id)
        {
            case -1:
                Changepwdstr = context.getResources().getString(R.string.Wrong_AppID_Type);
                break;
            case -2:
                Changepwdstr = context.getResources().getString(R.string.Ac_Or_Pwd_Length_Err_Type);
                break;
            case -3:
                Changepwdstr = context.getResources().getString(R.string.Ac_Or_Pwd_Err_Type);
                break;
            case -4:
                Changepwdstr = context.getResources().getString(R.string.New_Pwd_Length_Err_Type);
                break;
            case -5:
                Changepwdstr = context.getResources().getString(R.string.Change_Pwd_Or_Contact_Customer_Service_Type);
                break;
            case -6:
                Changepwdstr = context.getResources().getString(R.string.Ac_With_Illegal_Char_Type);
                break;
            case -7:
                Changepwdstr = context.getResources().getString(R.string.Pwd_With_Illegal_Char_Type);
                break;
            case -8:
                Changepwdstr = context.getResources().getString(R.string.New_Pwd_With_Illegal_Char_Type);
                break;
            case 1:
                Changepwdstr = context.getResources().getString(R.string.Success_Change_Pwd_Type);
                break;
            case -97:
                Changepwdstr = context.getResources().getString(R.string.Checksum_Err_Type);
                break;
            case -98:
                Changepwdstr = context.getResources().getString(R.string.System_Err_Type);
                break;
        }
        return Changepwdstr;
    }
    
    public static String getDialogContentString(Context context, String str){
    	String Content ="";
    	
    	switch(str){
    	case "Google":
    		Content = context.getResources().getString(R.string.GG_DIALOG_CONTENT_TEXT);
    		break;
    	case "Facebook":
    		Content = context.getResources().getString(R.string.FB_DIALOG_CONTENT_TEXT);
    		break;
    	}
    	
    	return Content;
    }
}
