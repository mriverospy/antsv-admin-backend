package py.gov.mitic.htv.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
public class PermissionChecker implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(authentication, permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(auth, permission.toString());
    }

    private boolean hasPrivilege(Authentication auth, String permission) {
        if(auth != null && auth.getAuthorities().size() > 0) {
            for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}