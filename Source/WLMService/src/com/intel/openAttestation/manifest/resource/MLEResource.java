/*
Copyright (c) 2012, Intel Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of Intel Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.intel.openAttestation.manifest.resource;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import com.intel.openAttestation.manifest.bean.MLEBean;
import com.intel.openAttestation.manifest.bean.MLE_Manifest;
import com.intel.openAttestation.manifest.bean.OpenAttestationResponseFault;
import com.intel.openAttestation.manifest.hibernate.dao.MLEDAO;
import com.intel.openAttestation.manifest.hibernate.dao.OEMDAO;
import com.intel.openAttestation.manifest.hibernate.dao.OSDAO;
import com.intel.openAttestation.manifest.hibernate.dao.PcrWhiteListDAO;
import com.intel.openAttestation.manifest.hibernate.domain.MLE;
import com.intel.openAttestation.manifest.hibernate.domain.OEM;
import com.intel.openAttestation.manifest.hibernate.domain.OS;
import com.intel.openAttestation.manifest.hibernate.domain.PcrWhiteList;
import com.intel.openAttestation.manifest.resource.MLEResource;


/**
 * RESTful web service interface to work with MLE DB.
 * @author 
 *
 */

@Path("resources/mles")
public class MLEResource {
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addMLE(@Context UriInfo uriInfo, MLEBean mleBean,
			@Context javax.servlet.http.HttpServletRequest request){
        UriBuilder b = uriInfo.getBaseUriBuilder();
        b = b.path(MLEResource.class);
		Response.Status status = Response.Status.OK;
		try{
			    MLEDAO dao = new MLEDAO();
			    PcrWhiteListDAO pcrDao = new PcrWhiteListDAO();
			    OSDAO osDao = new OSDAO();
			    OEMDAO oemDao = new OEMDAO();
			    MLE mle = new MLE();
			    OS os = new OS();
			    OEM oem = new OEM();
			    List<PcrWhiteList> pcrs = new ArrayList(); 
			    
			    if (mleBean.getMLE_Type().equals("VMM")){
			    	System.out.println("The OS Name exists:" + mleBean.getOsName());
			    	if ( (os = osDao.getOS(mleBean.getOsName(), mleBean.getOsVersion()))!= null){
			    		mle.setOs(os);
			    	}
			    	else{
						status = Response.Status.BAD_REQUEST;
						OpenAttestationResponseFault fault = new OpenAttestationResponseFault(1006);
						fault.setError_message("Data Error - OS[" + os.getName() + 
								"] Version[" +os.getVersion() +"] does not exist");
						return Response.status(status).header("Location", b.build()).entity(fault)
									.build();
			    	}
			    }
			    else if(mleBean.getMLE_Type().equals("BIOS")){
			    	if((oem = oemDao.getOEM(mleBean.getOemName())) != null){
			    		mle.setOem(oem);
			    	}
			    	else{
						status = Response.Status.BAD_REQUEST;
						OpenAttestationResponseFault fault = new OpenAttestationResponseFault(1006);
						fault.setError_message("Data Error - OEM[" + oem.getName() + 
								"] Description[" +oem.getDescription() +"] does not exist");
						return Response.status(status).header("Location", b.build()).entity(fault)
									.build();
						
				    }
			    }
			    else{
			    	status = Response.Status.BAD_REQUEST;
					OpenAttestationResponseFault fault = new OpenAttestationResponseFault(1006);
					fault.setError_message("Data Error - MLE_Type is error:" + mleBean.getMLE_Type());
					return Response.status(status).header("Location", b.build()).entity(fault)
								.build();
			    }
			    
			    mle.setName(mleBean.getName());
			    mle.setVersion(mleBean.getVersion());
			    mle.setAttestation_Type(mleBean.getAttestation_Type());
			    mle.setDescription(mleBean.getDescription());
			    mle.setMLE_Type(mleBean.getMLE_Type());
				dao.addMLEEntry(mle);
				if (mleBean.getMLE_Manifests()!=null){
					for(MLE_Manifest mle_manifest:mleBean.getMLE_Manifests()){
						PcrWhiteList pcr = new PcrWhiteList();
						pcr.setPcrName(mle_manifest.getName());
						pcr.setPcrDigest(mle_manifest.getValue());
						pcr.setMle(mle);
						pcrDao.addPcrEntry(pcr);
					}
					
				}
		        return Response.status(status).header("Location", b.build()).type(MediaType.TEXT_PLAIN).entity("True")
		        		.build();
			}catch (Exception e){
				status = Response.Status.INTERNAL_SERVER_ERROR;
				OpenAttestationResponseFault fault = new OpenAttestationResponseFault(
						OpenAttestationResponseFault.FaultCode.FAULT_500);
				fault.setError_message("Add MLE entry failed." + "Exception:" + e.getMessage());
				return Response.status(status).header("Location", b.build()).entity(fault)
						.build();
		}

	}
	
	@PUT
    @Consumes("application/json")
    @Produces("application/json")
	public Response updateMLE(@Context UriInfo uriInfo, MLEBean mleBean,
			@Context javax.servlet.http.HttpServletRequest request){
        UriBuilder b = uriInfo.getBaseUriBuilder();
        b = b.path(MLEResource.class);
		Response.Status status = Response.Status.OK;
		try{
			MLEDAO mleDao = new MLEDAO();
			OSDAO osDao = new  OSDAO();
			OEMDAO oemDao =new OEMDAO();
			PcrWhiteListDAO pcrDao = new PcrWhiteListDAO();
			
			System.out.println("Check if the MLE exists:" + mleBean.getName());
			
			MLE mle = mleDao.getMLE(mleBean.getName(), mleBean.getVersion());
			if (mle != null)
			{
				//OS
				if (mleBean.getOsName() != null && mleBean.getOsVersion() != null){
					OS os = osDao.getOS(mleBean.getOsName(), mleBean.getOsVersion());
					if( os == null )
						throw new Exception ("os not found");
				}
				//OEM
				else if (mleBean.getOemName() != null){
					OEM oem = oemDao.getOEM(mleBean.getOemName());
					if( oem == null )
						throw new Exception ("oem not found");
				}
				//throw
				else{
					throw new Exception ("oem | os not found");
				}
				//desc
				System.out.println(" updateMLE.DESCRIPTION:" + mleBean.getDescription());
				mleDao.editMLEDesc(mle, mleBean.getDescription());
				//white list
				if (mleBean.getMLE_Manifests()!=null){
					for(MLE_Manifest mle_manifest:mleBean.getMLE_Manifests()){
						System.out.println("##pcr name = " + mle_manifest.getName() + "mle id = " + mle.getMLEID() );
						if( pcrDao.isPcrExisted(mle_manifest.getName(), mle.getMLEID()))
						{
							System.out.println("##Del : pcr name = " + mle_manifest.getName() + "mle id = " + mle.getMLEID() );
							pcrDao.deletePcrEntry(mle_manifest.getName(), mle.getMLEID());
						}
					    System.out.println("##ADD: MLE_ID = " +  mle.getMLEID() + "PCR_NAME = " + mle_manifest.getName());
						PcrWhiteList addPcr = new PcrWhiteList();
						addPcr.setPcrName(mle_manifest.getName());
						addPcr.setPcrDigest(mle_manifest.getValue());
						addPcr.setMle(mle);
						pcrDao.editPcrEntry(addPcr);
					}
				}
				
			}
			
			return Response.status(status).header("Location", b.build()).type(MediaType.TEXT_PLAIN).entity("True")
	        		.build();
		}catch (Exception e){
			status = Response.Status.INTERNAL_SERVER_ERROR;
			OpenAttestationResponseFault fault = new OpenAttestationResponseFault(
					OpenAttestationResponseFault.FaultCode.FAULT_500);
			fault.setError_message("Add MLE entry failed." + "Exception:" + e.getMessage());
			return Response.status(status).header("Location", b.build()).entity(fault)
					.build();
	}
  }
		
	@DELETE
	@Produces("application/json")
	public Response delMLEEntry(@QueryParam("mleName") String name, @QueryParam("mleVersion") String version, @QueryParam("osName") String osName, @QueryParam("osVersion") String osVersion,@QueryParam("oemName") String oemName,@Context UriInfo uriInfo){
        UriBuilder b = uriInfo.getBaseUriBuilder();
        b = b.path(MLEResource.class);
		Response.Status status = Response.Status.OK;

        try{	
        	System.out.println("##Check name:" + name);
        	System.out.println("##Check version:" + version);
        	System.out.println("##Check name:" + osName);
        	System.out.println("##Check version:" + osVersion);
        	System.out.println("##Check name:" + oemName);
        	
        	MLEDAO mleDao = new MLEDAO();
            MLE mle = mleDao.getMLE(name, version);
			if (mle != null)
			    mleDao.DeleteMLEEntry(name, version);
			else
				throw new Exception ("mle not found");
			
			PcrWhiteListDAO pcrDao = new PcrWhiteListDAO();
			System.out.println("##Check mle id:" + mle.getMLEID());
			pcrDao.deletePcrByMleID(mle.getMLEID());
        	
        	return Response.status(status).header("Location", b.build()).type(MediaType.TEXT_PLAIN).entity("True")
            		.build();

		}catch (Exception e){
			status = Response.Status.INTERNAL_SERVER_ERROR;
			OpenAttestationResponseFault fault = new OpenAttestationResponseFault(
					OpenAttestationResponseFault.FaultCode.FAULT_500);
			fault.setError_message("Delete MLE entry failed." + "Exception:" + e.getMessage()); 
			return Response.status(status).entity(fault)
					.build();

		}
	}

   
}
