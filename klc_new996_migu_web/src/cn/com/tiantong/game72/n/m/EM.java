package cn.com.tiantong.game72.n.m;


/**
 * 
 * @author wujianheng
 *
 */
public class EM {

	public static final int OBJECT_NULL_ERROR = 0x00000100;
	public static final int STRING_NULL_ERROR = 0x00000101;
	public static final int DATA_NULL_ERROR = 0x00000102;
	public static final int ID_ILLEGAL_ERROR = 0x00000103;
	public static final int EMAIL_NULL_ERROR = 0x00000104;
	public static final int EMAIL_FORMAT_ERROR = 0x00000105;
	public static final int PASSWORD_NULL_ERROR = 0x00000106;
	public static final int NICKNAME_NULL_ERROR = 0x00000107;
	public static final int DATE_FORMAT_ERROR = 0x00000108;
	public static final int DATETIME_FORMAT_ERROR = 0x00000109;
	public static final int INDEX_ILLEGAL_ERROR = 0x00000110;
	public static final int DATAREQUEST_FAIL_ERROR = 0x00000111;
	public static final int DATAPARSE_FAIL_ERROR = 0x00000112;
	public static final int DATA_EXCEPTION_ERROR = 0x00000113;
	public static final int NETWORK_EXCEPTION_ERROR = 0x00000114;
	
	public static String getErrorInfo(int errorcode)
	{
		String msgstr = "";
		
		switch(errorcode)
		{
		case OBJECT_NULL_ERROR:
			msgstr = "数据为空!";
			break;
		case STRING_NULL_ERROR:
			msgstr = "字符串为�?";
			break;
		case DATA_NULL_ERROR:
			msgstr = "数据为空!";
			break;
		case ID_ILLEGAL_ERROR:
			msgstr = "ID非法!";
		case EMAIL_NULL_ERROR:
			msgstr = "邮箱地址为空!";
			break;
		case EMAIL_FORMAT_ERROR:
			msgstr = "邮箱地址格式错误!";
			break;
		case PASSWORD_NULL_ERROR:
			msgstr = "密码为空!";
			break;
		case NICKNAME_NULL_ERROR:
			msgstr = "昵称为空!";
			break;
		case DATE_FORMAT_ERROR:
			msgstr = "日期格式错误!";
			break;
		case DATETIME_FORMAT_ERROR:
			msgstr = "时间格式错误!";
			break;
		case INDEX_ILLEGAL_ERROR:
			msgstr = "索引错误!";
			break;
		case DATAREQUEST_FAIL_ERROR:
			msgstr = "网络数据请求失败!";
			break;
		case DATAPARSE_FAIL_ERROR:
			msgstr = "数据解析失败!";
			break;
		case DATA_EXCEPTION_ERROR:
			msgstr = "数据异常!";
			break;
		case NETWORK_EXCEPTION_ERROR:
			msgstr = "网络异常!";
			break;
		default:
			break;
		}
		return msgstr;
	}
}
