package com.crpc.core.serialize.cprc;


import com.crpc.core.serialize.SerializeFactory;
import com.crpc.core.serialize.User;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * @author liuhuaicong
 * @date 2023/10/18
 */
public class CRpcSerializeFactory implements SerializeFactory {

    class ByteHolder {
        private byte[] bytes;

        public ByteHolder(byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    @Override
    public <T> byte[] serialize(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        List<ByteHolder> byteHolderList = new ArrayList<>();
        int totalSize = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object orgVal = field.get(t);
                byte[] arr = this.getByteArrayByField(field,orgVal);
                totalSize+=arr.length;
                byteHolderList.add(new ByteHolder(arr));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        byte[] result = new byte[totalSize];
        int index = 0;
        for (ByteHolder byteHolder : byteHolderList) {
            System.arraycopy(byteHolder.getBytes(),0,result,index,byteHolder.getBytes().length);
            index+=byteHolder.getBytes().length;
        }
        return result;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return null;
    }

    public byte[] getByteArrayByField(Field field, Object orgVal) {
        Class type = field.getType();
        if("java.lang.Integer".equals(type.getName())){
            return ByteConvertUtils.intToByte((Integer) orgVal);
        } else if("java.lang.Long".equals(type.getName())){
            return ByteConvertUtils.longToByte((Long) orgVal);
        } else if("java.lang.Short".equals(type.getName())){
            return ByteConvertUtils.shortToByte((Short) orgVal);
        } else if("java.lang.String".equals(type.getName())){
            if(orgVal==null){
                return new byte[0];
            }
            return ((String)orgVal).getBytes();
        }
        return new byte[0];
    }

    /**
     * System.arraycopy解析
     * <p>
     * src：byte源数组
     * srcPos：截取源byte数组起始位置（0位置有效）
     * dest,：byte目的数组（截取后存放的数组）
     * destPos：截取后存放的数组起始位置（0位置有效）
     * length：截取的数据长度
     */
    private byte[] userToByteArray(User user) {
        byte[] data = new byte[Integer.BYTES + Long.BYTES];
        int index = 0;
        System.arraycopy(ByteConvertUtils.intToByte(user.getId()), 0, data, index, Integer.BYTES);
        index += Integer.BYTES;
        System.arraycopy(ByteConvertUtils.longToByte(user.getTel()), 0, data, index, Long.BYTES);
        return data;
    }


    private User byteArrayToUser(byte[] bytes) {
        User user = new User();
        byte[] idBytes = new byte[Integer.BYTES];
        byte[] telBytes = new byte[Long.BYTES];
        System.arraycopy(bytes, 0, idBytes, 0, Integer.BYTES);
        System.arraycopy(bytes, Integer.BYTES, telBytes, 0, Long.BYTES);
        int id = ByteConvertUtils.byteToInt(idBytes);
        long tel = ByteConvertUtils.byteToLong(telBytes);
        user.setId(id);
        user.setTel(tel);
        return user;
    }


    public static void main(String[] args) {
        CRpcSerializeFactory serializeFactory = new CRpcSerializeFactory();
        User user = new User();
        user.setId(11);
        user.setTel(12L);
        byte[] r = serializeFactory.serialize(user);
        System.out.println(r.length);
        User user1 = serializeFactory.byteArrayToUser(r);
        System.out.println(user1);
    }
}
