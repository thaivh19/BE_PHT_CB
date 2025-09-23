package com.pht.service;

import java.util.List;

import com.pht.exception.BusinessException;


public interface IBaseService<O, T, I> {
	 	List<O> findAll();
	    O findById(I id) throws BusinessException;
	    O create(O entity) throws BusinessException;
	    void delete(I id) throws BusinessException;
	    O update(I id, O entity) throws BusinessException;
}
