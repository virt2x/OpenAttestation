/*
Copyright (c) 2012, Intel Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of Intel Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.intel.openAttestation.manifest.hibernate.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

import com.intel.openAttestation.manifest.hibernate.domain.HOST;
import com.intel.openAttestation.manifest.hibernate.domain.MLE;
import com.intel.openAttestation.manifest.hibernate.domain.OEM;
import com.intel.openAttestation.manifest.hibernate.domain.OS;
import com.intel.openAttestation.manifest.hibernate.util.HibernateUtilHis;


public class MLEDAO {

	/**
	 * Constructor to start a hibernate transaction in case one has not
	 * already been started 
	 */
	public MLEDAO() {
	}
	
	public MLE queryMLEidByNameAndVersionAndOEMid (String Name, String Version, String OEMname){
		List<MLE> mleList = null;
		try {
			HibernateUtilHis.beginTransaction();
			Query query = HibernateUtilHis.getSession().createQuery("select a from MLE a, OEM b where a.Name = :name and a.Version = :version and a.oem.OEMID = b.OEMID and b.Name = :oem_name");
			query.setString("name", Name);
			query.setString("version", Version);
			query.setString("oem_name", OEMname);
			List list = query.list();
			mleList = (List<MLE>)list;
			if (list.size() < 1) 
			{
				return null;
			} else {
				HibernateUtilHis.commitTransaction();
				return (MLE)mleList.get(0);
			}
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}
		
	}
	
	public MLE queryMLEidByNameAndVersionAndOSid (String Name, String Version, String OSname, String OSversion){
		List<MLE> mleList = null;
		try {
			HibernateUtilHis.beginTransaction();
			Query query = HibernateUtilHis.getSession().createQuery("select a from MLE a, OS b where a.Name = :name and a.Version = :version and a.os.ID = b.ID and b.Name = :os_name and b.Version = :os_version");
			query.setString("name", Name);
			query.setString("version", Version);
			query.setString("os_name", OSname);
			query.setString("os_version", OSversion);
			List list = query.list();
			mleList = (List<MLE>)list;
			if (list.size() < 1) 
			{
				return null;
			} else {
				HibernateUtilHis.commitTransaction();
				return (MLE)mleList.get(0);
			}
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}
		
	}

         public boolean isOEMExisted(String Name){
		boolean flag =false;
		try {
			HibernateUtilHis.beginTransaction();
			Query query = HibernateUtilHis.getSession().createQuery("from OEM a where a.Name = :value");
			query.setString("value", Name);
			List list = query.list();
		
			if (list.size() < 1) {
				flag =  false;
			} else {
				flag = true;
			}
			HibernateUtilHis.commitTransaction();
			return flag;
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}
	}
	
	
	
	public OEM addOEMEntry(OEM OEMEntry){
		try {
			HibernateUtilHis.beginTransaction();
			HibernateUtilHis.getSession().save(OEMEntry);
			HibernateUtilHis.commitTransaction();
			return OEMEntry;
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}

	}
	

	public MLE addMLEEntry(MLE MLEEntry){
		try {
			HibernateUtilHis.beginTransaction();
			HibernateUtilHis.getSession().save(MLEEntry);
			HibernateUtilHis.commitTransaction();
			return MLEEntry;
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}

	}
	

	public MLE getMLE(String Name,String Version){
		try{
		    MLE mle = null;
		    Query query = HibernateUtilHis.getSession().createQuery("from MLE m where m.Name = :name and m.Version = :version");
		    query.setString("name", Name);
		    query.setString("version", Version);
		    List list = query.list();
		    if (list.size() >= 1) {
		    	mle=(MLE)list.get(0);
			} 
		    return mle;
		}catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}
	}
	
	public void editMLEDesc(MLE mle, String Desc){
		try{
			HibernateUtilHis.beginTransaction();
			Session session = HibernateUtilHis.getSession();
		    Query query = HibernateUtilHis.getSession().createQuery("from MLE m where m.Name = :name and m.Version = :version");
		    query.setString("name", mle.getName());
		    query.setString("version", mle.getVersion());
            List list = query.list();
            if (list.size() < 1)
               throw new Exception ("Object not found");
            MLE mle2 = (MLE)list.get(0);
            mle2.setDescription(Desc);
            HibernateUtilHis.commitTransaction();
		}catch (Exception e) {
            HibernateUtilHis.rollbackTransaction();
            e.printStackTrace();
            throw new RuntimeException(e);
         }finally{
            HibernateUtilHis.closeSession();
         }
	}
	
	public HOST getHostById(Long Id){
		try {
			HOST host = null;
			Query query = HibernateUtilHis.getSession().createQuery("from HOST h where h.ID = :hostId");
			query.setLong("hostId", Id);
			List list = query.list();
			if (list.size() >= 1) {
				host=(HOST)list.get(0);
			} 
			return host;
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}
	}

	public void DeleteOEMEntry (String OEMName){
		try {
			HibernateUtilHis.beginTransaction();
			Session session = HibernateUtilHis.getSession();
			Query query = session.createQuery("from OEM a where a.Name = :NAME");
			query.setString("NAME", OEMName);
			List list = query.list();
			if (list.size() < 1){
				throw new Exception ("Object not found");
			}
			OEM OEMEntry = (OEM)list.get(0);
			session.delete(OEMEntry);
			HibernateUtilHis.commitTransaction();
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}
		
	}


	public void DeleteMLEEntry (String Name,String Version){
		try {
			HibernateUtilHis.beginTransaction();
			Session session = HibernateUtilHis.getSession();
			Query query = session.createQuery("from MLE a where a.Name = :NAME and a.Version = :VERSION");
			query.setString("NAME", Name);
			query.setString("VERSION", Version);
			List list = query.list();
			if (list.size() < 1){
				throw new Exception ("Object not found");
			}
			MLE MLEEntry = (MLE)list.get(0);
			session.delete(MLEEntry);
			HibernateUtilHis.commitTransaction();
		} catch (Exception e) {
			HibernateUtilHis.rollbackTransaction();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			HibernateUtilHis.closeSession();
		}
		
	}

}
