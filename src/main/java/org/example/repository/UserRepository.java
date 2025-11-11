package org.example.repository;

import org.example.model.Role;
import org.example.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserRepository {
    private Map<String, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
        initializeDefaulUsers();
    }
private void initializeDefaulUsers(){
    users.put("admin", new User("admin", "admin123", Role.ADMIN));
    users.put("manager", new User("manager", "manager123", Role.MANAGER));
    users.put("viewer", new User("viewer", "viewer123", Role.VIEWER));
}
   public User getUserByUsername(String username){
       return users.get(username);
   }
    public boolean addUser(User user){
    if(users.containsKey(user.getUsername())){
        return false;
    }
    users.put(user.getUsername(), user);
    return true;
    }
    public boolean updateUser(User user){
        if (!users.containsKey(user.getUsername())){
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }
    public boolean deleteUser(String username) {
        if (!users.containsKey(username)) {
            return false;
        }
        users.remove(username);
        return true;
    }
    public List<User> getAllUsers(){
      return new ArrayList<>(users.values());
}
//++++++++++Аутинтификация
    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}

