package me.zzp.jss.wrapper;

import me.zzp.jss.scope.Context;
import me.zzp.jss.scope.Page;
import me.zzp.jss.scope.Request;
import me.zzp.jss.scope.Response;
import me.zzp.jss.scope.Session;

public final class ScopeWrapper implements Wrapper {

    @Override
    public void wrap(Context context) throws Exception {
        context.setRequest(new Request(context));
        context.setResponse(new Response(context));
        context.setSession(new Session());
        context.setPage(new Page());
    }
}
