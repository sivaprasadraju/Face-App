package com.aconex.FaceRecognition.services;

import com.aconex.FaceRecognition.representation.CreatePersonBodyDto;
import com.aconex.FaceRecognition.representation.FaceIdentifyBodyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Base64;

@Service
public class FaceAPIServices {


    static String personGroupId = "interns-group-00";
    HttpClient httpclient = new DefaultHttpClient();
    static ObjectMapper objectMapper = new ObjectMapper();

    @Value("${subscriptionKey}")
    public String subscriptionKey;

    public HttpEntity faceDetectionRequestToFaceAPI(byte[] imageBytes){

        String faceDetectionURL="https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";

        try {

            URIBuilder builder = new URIBuilder(faceDetectionURL);
            builder.setParameter("returnFaceId", "true");

            URI uri = builder.build();

            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            ByteArrayEntity reqEntity = new ByteArrayEntity(imageBytes, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity httpEntity = response.getEntity();

            return httpEntity;

        }catch (Exception e){}

        return null;

    }

    public  HttpEntity faceIdentifyRequestToFaceAPI(String faceId){

        String faceIdentifyURL="https://westcentralus.api.cognitive.microsoft.com/face/v1.0/identify";

        try{

            URIBuilder builder = new URIBuilder(faceIdentifyURL);

            URI uri = builder.build();

            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            String[] faceIds=new String[1];
            faceIds[0]=faceId;

            FaceIdentifyBodyDto faceIdentifyBodyDto=new FaceIdentifyBodyDto(personGroupId,faceIds,"1","0.7");

            String body=objectMapper.writeValueAsString(faceIdentifyBodyDto);

            StringEntity reqEntity = new StringEntity(body);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity httpEntity = response.getEntity();

            return httpEntity;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public  HttpEntity createPersonRequestToFaceAPI(String emplyeeName,String designation){

        try {

            String cretePeronURL="https://westcentralus.api.cognitive.microsoft.com/face/v1.0/persongroups/" + personGroupId + "/persons";

            URIBuilder builder = new URIBuilder(cretePeronURL);
            URI uri = builder.build();

            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            CreatePersonBodyDto createPersonBodyDto=new CreatePersonBodyDto(emplyeeName,designation);

            String body=objectMapper.writeValueAsString(createPersonBodyDto);

            System.out.println("\n\n\n" + body + "\n\n\n");

            StringEntity reqEntity = new StringEntity(body);
            request.setEntity(reqEntity);

            HttpResponse httpResponse = httpclient.execute(request);
            HttpEntity httpEntity = httpResponse.getEntity();

            return httpEntity;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public void addImagesToPerson(String employeePersonId,String[] employeeImages){

        try {

            String addFacesToPersonURL = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/persongroups/interns-group-00/persons/" + employeePersonId + "/persistedFaces";

            URIBuilder builder = new URIBuilder(addFacesToPersonURL);

            int currentImage = 0;
            while (currentImage < employeeImages.length) {
                builder.setParameter("userData", "");
                builder.setParameter("targetFace", "");

                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

                byte[] imageBytes = Base64.getDecoder().decode(employeeImages[currentImage].substring(22));

                ByteArrayEntity reqEntity = new ByteArrayEntity(imageBytes, ContentType.APPLICATION_OCTET_STREAM);
                request.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(request);
                HttpEntity httpEntity = response.getEntity();
                EntityUtils.consume(httpEntity);
                currentImage++;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        trainPersonGroup();
    }
    public void trainPersonGroup(){

        String trainPersonGroupURL="https://westcentralus.api.cognitive.microsoft.com/face/v1.0/persongroups/"+personGroupId+"/train";

        try {
            URIBuilder builder = new URIBuilder(trainPersonGroupURL);

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            StringEntity reqEntity = new StringEntity("");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            EntityUtils.consume(response.getEntity());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}