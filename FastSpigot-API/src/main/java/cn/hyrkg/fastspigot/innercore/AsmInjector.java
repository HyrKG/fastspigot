package cn.hyrkg.fastspigot.innercore;

import cn.hyrkg.fastspigot.innercore.utils.ResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.objectweb.asm.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

@RequiredArgsConstructor
public class AsmInjector {
    public final FastInnerCore innerCore;

    /**
     * Cache of injected class to prevent duplicate inject.
     */
    private HashMap<Class<?>, Class<?>> injectedClassMap = new HashMap<>();

    /**
     * Inject some basic methods into class.
     *
     * @param clazz origin class.
     * @return injected class.
     */
    @SneakyThrows
    public <T> Class<T> inject(Class<T> clazz) {
        //return if was injected
        if (injectedClassMap.containsKey(clazz))
            return (Class<T>) injectedClassMap.get(clazz);

        //inner core path
        String innerCorePath = "cn/hyrkg/fastspigot/innercore/FastInnerCore";
        String urlPath = ResourceHelper.getPathAsUrl(clazz);
        String tag = "$handler" + "$" + innerCore.getCreator().getClass().getSimpleName();
        String tagPath = urlPath + tag;

        //asm inject
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        cw.visit(52, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, tagPath, null, urlPath, null);
        {
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "$innerCore", "L" + innerCorePath + ";", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(5, l0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, urlPath, "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(6, l1);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitFieldInsn(Opcodes.PUTFIELD, tagPath, "$innerCore", "L" + innerCorePath + ";");
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(5, l2);
            mv.visitInsn(Opcodes.RETURN);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable("this", "L" + tagPath + ";", null, l0, l3, 0);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getInnerCore", "()L" + innerCorePath + ";", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(10, l0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, tagPath, "$innerCore", "L" + innerCorePath + ";");
            mv.visitInsn(Opcodes.ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + tagPath + ";", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();
        Class<T> injectedClazz = null;

        if (injectedClazz == null) {
            //load injected class into classloader
            Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);

            byte[] bytes = cw.toByteArray();
            injectedClazz = (Class<T>) method.invoke(clazz.getClassLoader(), null, cw.toByteArray(), 0, bytes.length);
        }

        //cache injected class
        injectedClassMap.put(clazz, injectedClazz);
        return injectedClazz;
    }

    /**
     * Create a instance of injected class.
     *
     * @param clazz origin class.
     * @return instance of injected class.
     **/
    @SneakyThrows
    public <T> T createWithInjection(Class<T> clazz) {
        T obj = null;

        if (findInjectedClass(clazz) != null)
            obj = (T) findInjectedClass(clazz).newInstance();
        else
            obj = (T) inject(clazz).newInstance();

        Field field = obj.getClass().getDeclaredField("$innerCore");
        field.setAccessible(true);
        field.set(obj, innerCore);
        return obj;
    }

    public <T> Class<T> findInjectedClass(Class<T> originClass) {
        return (Class<T>) injectedClassMap.get(originClass);
    }


}
