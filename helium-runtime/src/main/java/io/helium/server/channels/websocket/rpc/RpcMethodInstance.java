package io.helium.server.channels.websocket.rpc;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.vertx.java.core.json.JsonObject;

import java.lang.annotation.Annotation;
import java.util.List;

class RpcMethodInstance {
    private Object instance;
    private java.lang.reflect.Method method;
    private ObjectConverter objectConverter = new ObjectConverter();

    public RpcMethodInstance(Object instance, java.lang.reflect.Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object call(JsonObject passedArgs) {
        try {
            List<Object> args = Lists.newArrayList();
            for (Annotation[] annotations : method.getParameterAnnotations()) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Rpc.Param) {
                        if (passedArgs != null) {
                            Rpc.Param param = (Rpc.Param) annotation;
                            if (passedArgs.containsField(param.value())) {
                                args.add(passedArgs.getValue(param.value()));
                            } else {
                                if (!Strings.isNullOrEmpty(param.defaultValue())) {
                                    args.add(param.defaultValue());
                                } else {
                                    args.add(null);
                                }
                            }
                        }
                    }
                }
            }
            Object[] argArray = createArray(method, args);
            return method.invoke(instance, argArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object[] createArray(java.lang.reflect.Method method, List<Object> args) {
        Object[] array = args.toArray();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            array[i] = objectConverter.convert(array[i], parameterTypes[i]);
        }
        return array;
    }
}