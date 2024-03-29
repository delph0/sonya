package com.sonya.sample.webapp.action;

import java.io.Serializable;

import com.sonya.sample.model.Person;
import com.sonya.service.GenericManager;
import com.sonya.webapp.action.BaseBean;

public class PersonForm extends BaseBean implements Serializable {
    private GenericManager<Person, Long> personManager;
    private Person person = new Person();
    private Long id;

    public void setPersonManager(GenericManager<Person, Long> manager) {
        this.personManager = manager;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String delete() {
        personManager.remove(person.getId());
        addMessage("person.deleted");

        return "list";
    }

    public String edit() {
        if (id != null) {
            person = personManager.get(id);
        } else {
            person = new Person();
        }

        return "edit";
    }

    public String save() {
        boolean isNew = (person.getId() == null);
        personManager.save(person);

        String key = (isNew) ? "person.added" : "person.updated";
        addMessage(key);

        if (isNew) {
            return "list";
        } else {
            return "edit";
        }
    }
}