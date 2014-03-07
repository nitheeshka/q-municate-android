package com.quickblox.qmunicate.ui.main;

import android.content.Context;
import android.os.Bundle;

import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.internal.module.custom.request.QBCustomObjectRequestBuilder;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.qmunicate.App;
import com.quickblox.qmunicate.core.ui.BaseLoader;
import com.quickblox.qmunicate.model.Friend;
import com.quickblox.qmunicate.ui.utils.Consts;

import java.util.ArrayList;
import java.util.List;

public class FriendListLoader extends BaseLoader<List<Friend>> {
    public static final int ID = 0;

    public FriendListLoader(Context context) {
        super(context);
    }

    public static Arguments newArguments(int page, int perPage) {
        Arguments arguments = new Arguments();
        arguments.page = page;
        arguments.perPage = perPage;
        return arguments;
    }

    @Override
    public List<Friend> performInBackground() throws QBResponseException {
        Arguments arguments = (Arguments) args;

        QBCustomObjectRequestBuilder builder = new QBCustomObjectRequestBuilder();
        builder.eq(Consts.FRIEND_FIELD_USER_ID, App.getInstance().getUser().getId());
        builder.setPagesLimit(arguments.perPage);
        int pagesSkip = arguments.perPage * (arguments.page - 1);
        builder.setPagesSkip(pagesSkip);

        List<QBCustomObject> objects = QBCustomObjects.getObjects(Consts.FRIEND_CLASS_NAME, builder);
        List<Integer> userIds = new ArrayList<Integer>();
        for (QBCustomObject o : objects) {
            userIds.add(Integer.parseInt((String) o.getFields().get(Consts.FRIEND_FIELD_FRIEND_ID)));
        }
        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(arguments.page);
        requestBuilder.setPerPage(arguments.perPage);

        Bundle params = new Bundle();
        List<QBUser> users = QBUsers.getUsersByIDs(userIds, requestBuilder, params);

        return Friend.createFriends(users);
    }

    private static class Arguments extends BaseLoader.Args {
        int page;
        int perPage;
    }
}
