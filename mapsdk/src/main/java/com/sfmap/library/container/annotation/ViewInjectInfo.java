package com.sfmap.library.container.annotation;

/**
 *
 */
public class ViewInjectInfo {
    public int value;
    public int parentId;

    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;

	ViewInjectInfo that = (ViewInjectInfo) o;

	if (parentId != that.parentId) return false;
	if (value != that.value) return false;

	return true;
    }

    @Override
    public int hashCode() {
	int result = value;
	result = 31 * result + parentId;
	return result;
    }
}
