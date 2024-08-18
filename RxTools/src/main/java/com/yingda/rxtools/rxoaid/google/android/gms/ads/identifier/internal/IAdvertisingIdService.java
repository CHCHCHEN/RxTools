package com.yingda.rxtools.rxoaid.google.android.gms.ads.identifier.internal;

/**
 * author: chen
 * data: 2024/8/18
 * des: 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题
 */
@SuppressWarnings("All")
public interface IAdvertisingIdService extends android.os.IInterface {
    /**
     * Default implementation for IAdvertisingIdService.
     */
    public static class Default implements IAdvertisingIdService {
        @Override
        public String getId() throws android.os.RemoteException {
            return null;
        }

        @Override
        public boolean isLimitAdTrackingEnabled(boolean boo) throws android.os.RemoteException {
            return false;
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IAdvertisingIdService {
        private static final String DESCRIPTOR = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService interface,
         * generating a proxy if needed.
         */
        public static IAdvertisingIdService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IAdvertisingIdService))) {
                return ((IAdvertisingIdService) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_getId: {
                    data.enforceInterface(descriptor);
                    String _result = this.getId();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_isLimitAdTrackingEnabled: {
                    data.enforceInterface(descriptor);
                    boolean _arg0;
                    _arg0 = (0 != data.readInt());
                    boolean _result = this.isLimitAdTrackingEnabled(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IAdvertisingIdService {
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
            public String getId() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getId, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getId();
                    }
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public boolean isLimitAdTrackingEnabled(boolean boo) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(((boo) ? (1) : (0)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_isLimitAdTrackingEnabled, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().isLimitAdTrackingEnabled(boo);
                    }
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public static IAdvertisingIdService sDefaultImpl;
        }

        static final int TRANSACTION_getId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_isLimitAdTrackingEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        public static boolean setDefaultImpl(IAdvertisingIdService impl) {
            // Only one user of this interface can use this function
            // at a time. This is a heuristic to detect if two different
            // users in the same process use this function.
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IAdvertisingIdService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    public String getId() throws android.os.RemoteException;

    public boolean isLimitAdTrackingEnabled(boolean boo) throws android.os.RemoteException;
}
