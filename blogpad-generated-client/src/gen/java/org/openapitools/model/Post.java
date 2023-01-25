package org.openapitools.model;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Post  {
  
  @ApiModelProperty(example = "openAPI intro", required = true, value = "")
  private String title;

  @ApiModelProperty(example = "how to use ....", required = true, value = "")
  private String content;

  @ApiModelProperty(value = "")
  private String fileName;

  @ApiModelProperty(value = "")
  private Date createdAt;

  @ApiModelProperty(value = "")
  private Date modifiedAt;
 /**
   * Get title
   * @return title
  **/
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Post title(String title) {
    this.title = title;
    return this;
  }

 /**
   * Get content
   * @return content
  **/
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Post content(String content) {
    this.content = content;
    return this;
  }

 /**
   * Get fileName
   * @return fileName
  **/
  @JsonProperty("fileName")
  public String getFileName() {
    return fileName;
  }


 /**
   * Get createdAt
   * @return createdAt
  **/
  @JsonProperty("createdAt")
  public Date getCreatedAt() {
    return createdAt;
  }


 /**
   * Get modifiedAt
   * @return modifiedAt
  **/
  @JsonProperty("modifiedAt")
  public Date getModifiedAt() {
    return modifiedAt;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Post post = (Post) o;
    return Objects.equals(title, post.title) &&
        Objects.equals(content, post.content) &&
        Objects.equals(fileName, post.fileName) &&
        Objects.equals(createdAt, post.createdAt) &&
        Objects.equals(modifiedAt, post.modifiedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, content, fileName, createdAt, modifiedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Post {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    modifiedAt: ").append(toIndentedString(modifiedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

