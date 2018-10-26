package com.vin.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vin.entity.Content;
import com.vin.entity.Novel;
import com.vin.exception.ControllerException;
import com.vin.service.NovelService;

@RestController
@RequestMapping("/novels")
public class NovelController {
	
	static Logger log = LogManager.getLogger( NovelController.class.getName());
	
    @Resource
    private NovelService novelService;

    @Value("${file.id.start}")
    private long fileId ;
    
    @Value("${novel.name.tag}")
    String nameTag;
    
    @Value("${novel.author.tag}")
    String authorTag;
    
    @Value("${novel.page.lines.num}")
    int pageLinesCnt;
    
    private String filePath = System.getProperty("user.dir") +File.separator+"upload";
    
    /**
     * By uploading text file to create a novel entity, also add this content pages.
     * @param file:  the novel text, include the author,the title and the content.
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String create(@RequestParam("file") MultipartFile file) {
    	return UpdateFileAndSaveNovel(file);
    }
    
    /**
     * list all novels here via JSon.  
     * @return  the novel entity and also its contents pages 
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<Novel> list() {
    	ArrayList<Novel> list = (ArrayList<Novel>)novelService.list();
    	return list;
    }
    
    /**
     * get novel info.
     * @param id: novel's id.
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Novel get(@PathVariable("id") Long id) {
        Novel nov = novelService.get(id);
        return nov;
    }
    
    /**
     * update a novel entity.
     * @param novel: novelInfo
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void update(@RequestBody Novel novel) {
	  novelService.save(novel);
    }
  
    /**
     * update a novel info.
     * @param id: novel's id.
     * @param novel: the novel info.
     */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable("id") Long id, @RequestBody Novel novel) {
		novel.setId(id);
		novelService.save(novel);
	}
	
	/**
	 * delete a novel entity and also delete it's contents.
	 * @param id
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Long id) {
		novelService.delete(id);
	}
  
	/**
	 * get novel's content page by novel id and page id.
	 * @param novelid: novel id.
	 * @param pageid: content page id.
	 * @throws FileNotFoundException 
	 */
	@RequestMapping(value = "/{novelid}/{pageid}", method = RequestMethod.GET)
	public String getContentPage(@PathVariable("novelid") Long novelid,@PathVariable("pageid") int pageid) throws FileNotFoundException {
		Novel novel = novelService.get(novelid);
		if(null == novel){
			return "no such novel.";
		}
		if(pageid>novel.getContents().size()){
			pageid = novel.getContents().size();
		}
		Content content = novel.getContents().get(pageid-1);
		
		// upload\1001-thevalleyofgold\1.txt
		String contentPath = content.getContentPath();
		String FileName = System.getProperty("user.dir") +File.separator+contentPath;
		
		File contentFile = new File(FileName);
		  String contentTxt = "";
          try {

              FileInputStream in=new FileInputStream(contentFile);

              int size=in.available();

              byte[] buffer=new byte[size];

              in.read(buffer);

              in.close();

              contentTxt=new String(buffer);

          } catch (Exception e) {
              e.printStackTrace();
              return contentTxt;
          }

          
          
		return contentTxt;
	}

    /**
     * Update a text file into the project's ./upload/ path, and also record it's contents into small text files.
     * @param file: the novel text.
     * @return
     * @throws ControllerException 
     */
    public String UpdateFileAndSaveNovel(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
            	
            	String fullFileName = filePath+File.separator+file.getOriginalFilename();
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(fullFileName)));
                out.write(file.getBytes());
                out.flush();
                out.close();
                SaveNovelInfo(fullFileName);
                
            } catch (Exception e) {
                e.printStackTrace();
                return "Upload Error." + e.getMessage();
            }
            return "Upload Successfully.";
        } else {
            return "Upload Error.file is empty";
        }
    }
    
    /**
     * Save novel info and contents.
     * @param fullFileName: the upload novel text file's path.
     * @throws ControllerException 
     */
    public void SaveNovelInfo(String fullFileName) throws ControllerException{
        try {
            // read file content from file
            StringBuffer sb= new StringBuffer("");
           
            FileReader reader = new FileReader(fullFileName);
            BufferedReader br = new BufferedReader(reader);
           
            String contentLine = null;
            String author = "";
            String name = "";
            String path = fullFileName;
            String fileId = this.fileId+"_";
            boolean hasAuthor = false;
            boolean hasName = false;
          
            int index = 0; 
            
            Novel novel = new Novel();
            while((contentLine = br.readLine()) != null) {
            	  index++;
                  sb.append(contentLine+ System.getProperty("line.separator"));
                  if(!hasName&&contentLine.contains(nameTag)){
                	  name = contentLine.substring(contentLine.indexOf(nameTag)+nameTag.length()).trim();
                	  hasName = true;
                	  
                	  fileId = this.fileId+"-"+name.toLowerCase().replaceAll(" ", "");
                	  this.fileId++;
                  }
                  if(!hasAuthor&&contentLine.contains(authorTag)){
                	  author = contentLine.substring(contentLine.lastIndexOf(authorTag)+authorTag.length()).trim();
                	  hasAuthor = true;
                  }
                  if(index%pageLinesCnt==0){
                	  Content content = new Content();
                	  content.setNovel(novel);
                	  content.setPageId(index/pageLinesCnt);
                	  content.setContentPath(this.filePath.replace(System.getProperty("user.dir"), "")+File.separator+fileId+File.separator +index/pageLinesCnt+".txt");
                	  novel.getContents().add(content);
                	  writeToFile(sb,fileId,index/pageLinesCnt);
                	  sb= new StringBuffer("");
                  }
            }
            novelService.save(novel);
            br.close();
            reader.close();
           
           
            if(!hasAuthor){
            	log.info("warn:no author info.");
            }
            if(!hasName){
            	name= new File(fullFileName).getName();
            }
            novel.setName(name);
            novel.setFileId(fileId);
            novel.setAuthor(author);
            novel.setPath(path.replace(System.getProperty("user.dir"), ""));
            novelService.save(novel);
      }catch(IOException e) {
            e.printStackTrace();
            throw new ControllerException(e.getMessage());
      }
    }
    
    /**
     * save contents into text files.
     * @param content: one page contents.
     * @param fileId: used to identify the file and make a path with it.
     * @param i: page number.
     * @throws ControllerException 
     */
	private void writeToFile(StringBuffer content, String fileId,  int i) throws ControllerException {
		String pathName = this.filePath+File.separator+fileId;
		String FileName = pathName+File.separator +i+".txt";
		File f = new File(pathName);
		if(!f.exists()){
			f.mkdir();
		}
		FileWriter writer;
		try {
			writer = new FileWriter(FileName);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(content.toString());
			bw.close();
			writer.close();
		}catch(IOException e) {
			e.printStackTrace();
			throw new ControllerException(e.getMessage());
		}
	}
}