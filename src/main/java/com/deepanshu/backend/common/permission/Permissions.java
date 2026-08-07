package com.deepanshu.backend.common.permission;

import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;

public final class Permissions {
    private Permissions(){};

    public static final WorkspaceRole[] WORKSPACE_READ = {
            WorkspaceRole.OWNER,
            WorkspaceRole.ADMIN,
            WorkspaceRole.MEMBER,
            WorkspaceRole.VIEWER
    };

    public static final WorkspaceRole[] WORKSPACE_WRITE = {
            WorkspaceRole.OWNER,
            WorkspaceRole.ADMIN,
    };


    public static final WorkspaceRole[] TASK_EDIT = {
            WorkspaceRole.OWNER,
            WorkspaceRole.ADMIN,
            WorkspaceRole.MEMBER
    };

    public static final WorkspaceRole[] OWNER_ONLY = {
            WorkspaceRole.OWNER
    };
}
