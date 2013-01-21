package org.qrone.login;

import java.security.Key;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


import org.apache.commons.codec.binary.Base64;

public class ID {
	
	private static UUID providerid = UUID.randomUUID();
	private static UUID providersecret = UUID.randomUUID();
	
	static AccessToken generateAccessToken(UUID userid, String scope){
		return new AccessToken(encryptOpenID(userid, providerid, providersecret), scope);
	}
	
	public static UUID decryptOpenID(String openid, UUID providersecret){
		if(openid.startsWith("ID-")){
			openid = openid.substring(3);
		}
		byte[] eoid = Base64.decodeBase64(openid);
		byte[] oid = decode4(eoid, uuid2byte(providersecret));
		return byte2uuid(splitBytes(oid)[0]);
	}
	
	public static String encryptOpenID(UUID id, UUID consumerid, UUID providersecret){
		byte[] oid = mergeBytes(uuid2byte(id), uuid2byte(consumerid));
		byte[] eoid = encode4(oid, uuid2byte(providersecret));
		return "ID-" +Base64.encodeBase64URLSafeString(eoid);
	}

	public static byte[][] splitBytes(byte[] openid){
		byte[][] ids = new byte[2][16];
		for (int i = 0; i < 16; i++) {
			ids[0][i] = openid[i*2];
		}
		for (int i = 0; i < 16; i++) {
			ids[1][i] = openid[i*2+1];
		}
		return ids;
	}
	
	public static byte[] mergeBytes(byte[] id1, byte[] id2){
		byte[] openid = new byte[32];
		for (int i = 0; i < 16; i++) {
			openid[i*2] = id1[i];
		}
		for (int i = 0; i < 16; i++) {
			openid[i*2+1] = id2[i];
		}
		return openid;
	}
	
	/**
	 * ˆÃ†‰»
	 */
	public static byte[] encode4(byte[] src, byte[] key) {
		try {
			Key skey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);

			byte[] enc = cipher.doFinal(src);
			return enc;
			
			//byte[] iv = cipher.getIV();
			//byte[] ret = new byte[iv.length + enc.length];
			//System.arraycopy(iv, 0, ret, 0, iv.length);
			//System.arraycopy(enc, 0, ret, iv.length, enc.length);
			//return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * •œ†‰»
	 */
	public static byte[] decode4(byte[] src, byte[] key) {
		try {
			Key skey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			final int BLOCK_SIZE = cipher.getBlockSize();
			
			cipher.init(Cipher.DECRYPT_MODE, skey);
			return cipher.doFinal(src, 0, src.length);

			/*
			AlgorithmParameters iv = AlgorithmParameters.getInstance("AES");
			byte[] ib = new byte[2 + BLOCK_SIZE];
			ib[0] = 4;
			ib[1] = (byte) BLOCK_SIZE;
			System.arraycopy(src, 0, ib, 2, BLOCK_SIZE);
			iv.init(ib);

			cipher.init(Cipher.DECRYPT_MODE, skey, iv);
			return cipher.doFinal(src, BLOCK_SIZE, src.length - BLOCK_SIZE);
			*/
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] uuid2byte(UUID uuid){
		byte[] b = new byte[16];
		printLong(b, 0, uuid.getMostSignificantBits());
		printLong(b, 8, uuid.getLeastSignificantBits());
		return b;
	}
	
	private static UUID byte2uuid(byte[] uuidbytes){
		return new UUID(getLong(uuidbytes, 0),getLong(uuidbytes, 8));
	}
	
	public static void printLong(byte[] data,int offset,long value) throws IllegalArgumentException{
		int size=Long.SIZE/Byte.SIZE;
		if(data==null||data.length<size||offset<0||data.length-size<offset)

			throw new IllegalArgumentException("Bat Param");
		else{
			for(int i=0;i<size;i++)
				data[offset+i]=Long.valueOf(value>>(Byte.SIZE*i)).byteValue();
		}
	}
	
	public static long getLong(byte[] data,int offset) throws IllegalArgumentException{
		long result=0;
		int size=Long.SIZE/Byte.SIZE;
		if(data==null||data.length<size||offset<0||data.length-size<offset)

			throw new IllegalArgumentException("Bat Param");
		else{
			for(int i=0;i<size;i++)

				result|=Integer.valueOf(data[offset+i]&0xff).longValue()<<(Byte.SIZE*i);
		}
		return result;
	}

	public static void main(String[] args){
		
		UUID q1 = UUID.randomUUID();
		System.out.println(q1.toString());
		
		UUID q2 = UUID.randomUUID();
		System.out.println(q2.toString());
		
		UUID q3 = UUID.randomUUID();
		System.out.println(q3.toString());
		
		String openid = encryptOpenID(q1, q2, q3);
		System.out.println(openid);
		
		UUID q4 = decryptOpenID(openid, q3);
		System.out.println(q4.toString());
		
		
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid.toString());
		
		byte[] b = new byte[16];
		printLong(b, 0, uuid.getMostSignificantBits());
		printLong(b, 8, uuid.getLeastSignificantBits());
		
		long m = getLong(b, 0);
		long l = getLong(b, 8);
		
		uuid = new UUID(m,l);
		System.out.println(uuid.toString());
	}
}
