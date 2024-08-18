package com.yingda.rxtools.rxoaid.oplus.stdid;

import com.yingda.rxtools.rxoaid.heytap.openid.IOpenID;

/**
 * author: chen
 * data: 2024/8/18
 * des: 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题
 */
public interface IStdID extends IOpenID {
    public static abstract class Stub extends IOpenID.Stub {
        private static final String DESCRIPTOR = "com.oplus.stdid.IStdID";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IStdID asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IStdID))) {
                return ((IStdID) iin);
            }
            return (IStdID)new Proxy(obj);
        }


        private static class Proxy implements IStdID {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public String getSerID(String pkgName, String sign, String type) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeString(sign);
                    _data.writeString(type);
                    boolean _status = mRemote.transact(IStdID.Stub.TRANSACTION_getSerID, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getSerID(pkgName, sign, type);
                    }
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
            public static IOpenID sDefaultImpl;
        }

        static final int TRANSACTION_getSerID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

        public static boolean setDefaultImpl(IOpenID impl) {
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IOpenID getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

}
