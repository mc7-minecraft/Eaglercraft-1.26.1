import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassPatchTool {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java ClassPatchTool <workspace-root>");
            System.exit(1);
        }

        Path root = Path.of(args[0]);
        Path trackerClass = root.resolve("com/mojang/blaze3d/platform/FramerateLimitTracker.class");

        byte[] original = Files.readAllBytes(trackerClass);
        byte[] patched = patchFramerateLimitTracker(original);

        if (original.length == patched.length && java.util.Arrays.equals(original, patched)) {
            System.out.println("No changes were applied.");
            return;
        }

        Files.write(trackerClass, patched);
        System.out.println("Patched: " + trackerClass);
    }

    private static byte[] patchFramerateLimitTracker(byte[] input) {
        ClassReader cr = new ClassReader(input);
        ClassWriter cw = new ClassWriter(cr, 0);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if ("OUT_OF_LEVEL_MENU_LIMIT".equals(name)) value = 30;
                if ("ICONIFIED_WINDOW_LIMIT".equals(name)) value = 3;
                if ("AFK_LIMIT".equals(name)) value = 15;
                if ("LONG_AFK_LIMIT".equals(name)) value = 3;
                if ("AFK_THRESHOLD_MS".equals(name)) value = 15000L;
                if ("LONG_AFK_THRESHOLD_MS".equals(name)) value = 120000L;
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                if ("getFramerateLimit".equals(name) && "()I".equals(descriptor)) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitIntInsn(int opcode, int operand) {
                            if ((opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH)) {
                                if (operand == 10) operand = 3;
                                else if (operand == 30) operand = 15;
                                else if (operand == 60) operand = 30;
                            }
                            super.visitIntInsn(opcode, operand);
                        }
                    };
                }

                if ("getThrottleReason".equals(name) && descriptor.startsWith("()L")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitLdcInsn(Object value) {
                            if (value instanceof Long v) {
                                if (v == 600000L) value = 120000L;
                                else if (v == 60000L) value = 15000L;
                            }
                            super.visitLdcInsn(value);
                        }
                    };
                }

                return mv;
            }
        };

        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}
