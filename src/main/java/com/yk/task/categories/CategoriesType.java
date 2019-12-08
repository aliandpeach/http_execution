package com.yk.task.categories;

import java.io.Serializable;

public class CategoriesType implements Serializable, Cloneable {
    private static final long serialVersionUID = -8007270211878656712L;

    private int pkid;

    private String uuid;

    private String url;

    private String name;

    private int page;

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public CategoriesType clone() {
        try {
            return (CategoriesType) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
