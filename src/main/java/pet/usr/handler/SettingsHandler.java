package pet.usr.handler;

public class SettingsHandler {

    private final static ThreadLocal<String> workspace = new ThreadLocal<String>();
    private final static ThreadLocal<String> user = new ThreadLocal<String>();
    //private final static ThreadLocal<EditingListener> editingListener = new ThreadLocal<EditingListener>();

    public static void initialize(final String workspace,
            final String user) {
        release();
        SettingsHandler.workspace.set(workspace);
        SettingsHandler.user.set(user);
        //SettingsHandler.editingListener.set(editingListener);
    }

    public static void release() {
        workspace.remove();
        user.remove();
      //  editingListener.remove();

    }

    public static String getWorkspace() {
        return workspace.get();
    }

    public static String getUser() {
        return user.get();
    }

    /*public static EditingListener getEditingListener() {
        return editingListener.get();
    }*/
}
