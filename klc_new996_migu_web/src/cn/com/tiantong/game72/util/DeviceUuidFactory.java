package cn.com.tiantong.game72.util;

import java.util.UUID;

import cn.com.tiantong.game72.util.DeviceUuidFactory;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceUuidFactory
{
  protected static final String PREFS_FILE = "device_id.xml";
  protected static final String PREFS_DEVICE_ID = "device_id";
  protected static DeviceUuidFactory uuidFactory;
  protected static UUID uuid;
  protected String strUUID;

  public static DeviceUuidFactory getInstance()
  {
    if (uuidFactory == null) {
      return DeviceUuidFactory.uuidFactory = new DeviceUuidFactory();
    }
    return uuidFactory;
  }

  public String getUuid(Context context)
  {
    if (uuid == null) {
      synchronized (DeviceUuidFactory.class) {
        if (uuid == null) {
          SharedPreferences prefs = context.getSharedPreferences("device_id.xml", 0);
          String id = prefs.getString("device_id", null);
          if (id != null)
          {
            uuid = UUID.fromString(id);
          } else {
            uuid = UUID.randomUUID();

            prefs.edit().putString("device_id", uuid.toString()).commit();
          }
        }
      }
    }
    this.strUUID = uuid.toString().replace("-", "");
    return this.strUUID;
  }
}
