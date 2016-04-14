/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.navi.location.am;
import com.example.newpie.IndexActivity;
import com.example.newpie.bean.MapUserTrail;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * This class notifies the receiver of incoming notifcation packets
 * asynchronously.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationPacketListener implements PacketListener {

	private static final String LOGTAG = LogUtil
			.makeLogTag(NotificationPacketListener.class);

	private final XmppManager xmppManager;

	public NotificationPacketListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	@Override
	public void processPacket(Packet packet) {
		Log.d(LOGTAG, "NotificationPacketListener.processPacket()...");
		Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());
		JSONObject joGet = null;
		JSONArray ja = null;
		JSONObject jo = null;
		String currTime = new String();
		if (packet instanceof NotificationIQ) {
			NotificationIQ notification = (NotificationIQ) packet;

			if (notification.getChildElementXML().contains(
					"androidpn:iq:notification")) {
				String notificationId = notification.getId();
				String notificationApiKey = notification.getApiKey();
				String notificationTitle = notification.getTitle();
				String notificationMessage = notification.getMessage();
				// String notificationTicker = notification.getTicker();
				String notificationUri = notification.getUri();
				try {
					joGet = new JSONObject(notificationMessage);
					ja = joGet.getJSONArray("mapUserTrailList");

					currTime = joGet.getString("currTime");
				
				} catch (JSONException e) {
				}
				try {
					jo = (JSONObject) ja.get(0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if ("notification".equals(jo.getString("reachTime"))) {
						Intent intent = new Intent(
								Constants.ACTION_SHOW_NOTIFICATION);
						intent.putExtra(Constants.NOTIFICATION_ID,
								notificationId);
						intent.putExtra(Constants.NOTIFICATION_API_KEY,
								notificationApiKey);
						intent.putExtra(Constants.NOTIFICATION_TITLE,
								notificationTitle);
						intent.putExtra(Constants.NOTIFICATION_MESSAGE,
								"请开启上传导航");
						intent.putExtra(Constants.NOTIFICATION_URI,
								notificationUri);
						intent.putExtra(Constants.CALLBACK_ACTIVITY_CLASS_NAME, IndexActivity.class);
						xmppManager.getContext().sendBroadcast(intent);
					} else if ("killuser".equals(jo.getString("reachTime"))) {

						Intent intent1 = new Intent(
								"AndroidPnBoardKill");
						intent1.putExtra(Constants.NOTIFICATION_ID,
								notificationId);
						intent1.putExtra(Constants.NOTIFICATION_API_KEY,
								notificationApiKey);
						intent1.putExtra(Constants.NOTIFICATION_TITLE,
								notificationTitle);
						intent1.putExtra(Constants.NOTIFICATION_MESSAGE,
								"有人登陆你的账号，请查验");
						intent1.putExtra(Constants.NOTIFICATION_URI,
								notificationUri);
						xmppManager.getContext().sendBroadcast(intent1);
						
					} else {

						Intent mIntent = new Intent("AndroidPnBoardCast");
						List<Map<String, Object>> myMapUserTrailList = new ArrayList<Map<String, Object>>();
						MapUserTrail mt = new MapUserTrail();
						for (int i = 0; i < ja.length(); i++) {
							Map<String, Object> map = new HashMap<String, Object>();
							if (!"2".equals(ja.getJSONObject(i).getString(
									"ifPhotomark"))) {
								map.put("currUserId",
										(null != ja.getJSONObject(i).getString(
												"currUserId")) ? ja
												.getJSONObject(i).getString(
														"currUserId") : "_");
								map.put("currUserName",
										(null != ja.getJSONObject(i).getString(
												"currUserName")) ? ja
												.getJSONObject(i).getString(
														"currUserName") : "_");
								map.put("latitude", ja.getJSONObject(i)
										.getDouble("latitude"));
								map.put("longitude", ja.getJSONObject(i)
										.getDouble("longitude"));
								map.put("reachTime",
										(null != ja.getJSONObject(i).getString(
												"reachTime")) ? ja
												.getJSONObject(i).getString(
														"reachTime") : "_");
								map.put("ifPhotomark", ja.getJSONObject(i)
										.getString("ifPhotomark"));
								map.put("photoId", ja.getJSONObject(i)
										.getString("photoId"));
								map.put("photoTime", ja.getJSONObject(i)
										.getString("d"));
								map.put("currUserCompany",
										(null != ja.getJSONObject(i).getString(
												"currUserCompany")) ? ja
												.getJSONObject(i).getString(
														"currUserCompany")
												: "_");
								map.put("photoNum",
										ja.getJSONObject(i).getInt("photoNum"));
								map.put("stopTime", ja.getJSONObject(i)
										.getString("stopTime"));

								map.put("photoIfIndex", ja.getJSONObject(i)
										.getString("photoIfIndex"));
								map.put("photoUrl", ja.getJSONObject(i)
										.getString("photoUrl"));
								map.put("currUserDepartment", ja.getJSONObject(i)
										.getString("currUserDepartment"));
								myMapUserTrailList.add(map);
							}
						}
						mt.setMapUserTrailList(myMapUserTrailList);

						Map<String, String> timeMap = new HashMap<String, String>();
						timeMap.put("currYear", currTime.subSequence(0, 4)
								.toString());
						timeMap.put("currMonth", currTime.subSequence(4, 6)
								.toString());
						timeMap.put("currDate", currTime.subSequence(6, 8)
								.toString());
						mt.setCurrTime(timeMap);
						mIntent.putExtra("mt", mt);
						xmppManager.getContext().sendBroadcast(mIntent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

	}

}
