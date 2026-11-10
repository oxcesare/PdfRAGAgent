package com.search.content.ragagent.model;

import java.util.Map;

public record ArticleRecord(String id, String content, Map<String, Object> metadata) {}