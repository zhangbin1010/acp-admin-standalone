package pers.acp.admin.repo

import pers.acp.admin.base.BaseRepository
import pers.acp.admin.entity.ModuleFunc
import java.util.*

/**
 * @author zhangbin by 2018-1-17 17:46
 * @since JDK 11
 */
interface ModuleFuncRepository : BaseRepository<ModuleFunc, String> {

    fun findByCode(code: String): Optional<ModuleFunc>

    fun findByCodeAndIdNot(code: String, id: String): Optional<ModuleFunc>

    fun findByAppId(appId: String): MutableList<ModuleFunc>

    fun findByParentIdIn(idList: MutableList<String>): MutableList<ModuleFunc>

    fun deleteByIdInAndCovert(idList: MutableList<String>, covert: Boolean)

}
