package cn.hyrkg.fastspigot.spigot.service.simplemysql;

import cn.hyrkg.fastspigot.innercore.framework.interfaces.IServiceProvider;
import me.kg.fast.inject.mysql3_1.SimpleMysqlPool;
import me.kg.fast.inject.mysql3_1.UnsafeQuery;

public interface ISimpleMysql extends IServiceProvider {

    default UnsafeQuery mysql(String value) throws Exception {
        return new UnsafeQuery(value, getPool());
    }

    SimpleMysqlPool getPool();

}
