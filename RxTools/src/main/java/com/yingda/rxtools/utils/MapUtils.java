package com.yingda.rxtools.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {
    public static int GetIntFromMap(Map map, String key) {
        int reVal = 0;
        if (map != null) {
            if (map.containsKey(key)) {
                Object valObj = map.get(key);
                try {
                    reVal = (int) valObj;
                } catch (Exception e) {
                    try {
                        reVal = Integer.parseInt(valObj + "");
                    } catch (Exception e1) {
                    }
                }
            }
        }
        return reVal;
    }

    public static long GetLongFromMap(Map map, String key) {
        long reVal = 0;
        if (map != null) {
            if (map.containsKey(key)) {
                Object valObj = map.get(key);
                try {
                    reVal = (long) valObj;
                } catch (Exception e) {
                    try {
                        reVal = Long.parseLong(valObj + "");
                    } catch (Exception e1) {
                    }
                }
            }
        }
        return reVal;
    }

    public static Map GetMapFromObj(Object obj) {
        Map reMap = null;

        if (obj != null) {
            if (obj instanceof Map) {
                reMap = (Map) obj;
            } else {
                try {
                    reMap = JsonUtil.fromJson(HashMap.class, JsonUtil.toJson(obj));
                } catch (Exception e) {
                    reMap = new HashMap();
                }
            }
        }

        return reMap;
    }

    public static float GetFloatFromMap(Map map, String key) {
        float reVal = 0f;
        if (map != null) {
            if (map.containsKey(key)) {
                Object valObj = map.get(key);
                try {
                    reVal = (float) valObj;
                } catch (Exception e) {
                    try {
                        reVal = Float.parseFloat(valObj + "");
                    } catch (Exception e1) {
                    }
                }
            }
        }
        return reVal;
    }

    public static double GetDoubleFromMap(Map map, String key) {
        double reVal = 0;
        if (map != null) {
            if (map.containsKey(key)) {
                Object valObj = map.get(key);
                try {
                    reVal = (double) valObj;
                } catch (Exception e) {
                    try {
                        reVal = Double.parseDouble((valObj + ""));
                    } catch (Exception e1) {
                    }
                }
            }
        }
        return reVal;
    }

    public static boolean GetBooleanFromMap(Map map, String key) {
        boolean reVal = false;
        if (map != null) {
            if (map.containsKey(key)) {
                try {
                    reVal = (boolean) map.get(key);
                } catch (Exception e) {
                }
            }
        }
        return reVal;
    }

    public static String GetStringFromMap(Map map, String key) {
        String reVal = "";
        if (map != null) {
            if (map.containsKey(key)) {
                try {
                    reVal = map.get(key) + "";
                } catch (Exception e) {
                }
            }
        }
        return reVal;
    }

    public static String GetStringFromMap(Map map, String key, String defStr) {
        String reVal = defStr;
        if (map != null) {
            if (map.containsKey(key)) {
                try {
                    reVal = map.get(key) + "";
                } catch (Exception e) {
                }
            }
        }
        return reVal;
    }


    public static List<?> getListFromMap(Class<?> eleType, Map map, String key) {
        List<?> reVal = new ArrayList<>();
        if (map != null) {
            if (map.containsKey(key)) {
                try {
                    //reVal = (List<?>) map.get(key);
                    reVal = JsonUtil.fromJsonAsList(eleType, map.get(key) + "");
                } catch (Exception e) {
                }
            }
        }
        return reVal;
    }

    public static Map GetMapFromMap(Map map, String key) {
        Map reMap = null;

        Object obj = map.get(key);
        if (obj != null) {
            if (obj instanceof Map) {
                reMap = (Map) obj;
            } else {
                try {
                    reMap = JsonUtil.fromJson(Map.class, JsonUtil.toJson(obj));
                } catch (Exception e) {
                    reMap = new HashMap();
                }
            }
        }

        return reMap;
    }
}
