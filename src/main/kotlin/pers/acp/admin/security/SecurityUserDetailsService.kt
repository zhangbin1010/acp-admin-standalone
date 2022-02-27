package pers.acp.admin.security

import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.domain.ModuleFuncDomain
import pers.acp.admin.domain.UserDomain

/**
 * @author zhangbin by 11/04/2018 15:19
 * @since JDK 11
 */
@Component
class SecurityUserDetailsService @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val userDomain: UserDomain,
    private val moduleFuncDomain: ModuleFuncDomain
) : UserDetailsService {

    /**
     * 根据 username 获取用户信息
     *
     * @param username 用户名
     * @return 用户对象
     * @throws UsernameNotFoundException 找不到用户信息异常
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails =
        userDomain.getUserInfoByLoginNo(username, true).let { user ->
            if (user == null) {
                logAdapter.error("无此用户：$username")
                throw UsernameNotFoundException("无此用户：$username")
            }
            val grantedAuthorities: MutableSet<GrantedAuthority> = mutableSetOf()
            user.roleSet.forEach { role ->
                grantedAuthorities.add(SimpleGrantedAuthority(RoleCode.prefix + role.code)) //角色编码
            }
            moduleFuncDomain.getModuleFuncList(user.id).forEach { module ->
                grantedAuthorities.add(SimpleGrantedAuthority(module.code)) //模块功能编码
            }
            User(user.loginNo, user.password, user.enabled, true, true, true, grantedAuthorities)
        }
}
