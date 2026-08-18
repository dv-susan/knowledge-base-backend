package com.knowledge.base.document.service.impl;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 PPTX XML 标签平衡器
 */
class FileManagementServiceImplTest {

    /**
     * 通过反射调用私有方法 balanceXmlTags
     */
    private String invokeBalanceXmlTags(String xml) throws Exception {
        FileManagementServiceImpl impl = new FileManagementServiceImpl();
        Method method = FileManagementServiceImpl.class.getDeclaredMethod("balanceXmlTags", String.class);
        method.setAccessible(true);
        return (String) method.invoke(impl, xml);
    }

    /**
     * 验证 XML 能否被 XML 解析器成功解析
     */
    private void assertValidXml(String xml) {
        try {
            DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            fail("修复后的 XML 无法解析：" + e.getMessage() + "\nXML:\n" + xml);
        }
    }

    // ==================== 测试用例 ====================

    @Test
    void testMissingPTxBodyCloseBeforePSp() throws Exception {
        // <p:txBody> 缺少 </p:txBody>，在 </p:sp> 闭合前应补全
        String xml = "<?xml version=\"1.0\"?>\n"
                + "<p:sld xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n"
                + "  <p:cSld>\n"
                + "    <p:sp>\n"
                + "      <p:txBody>\n"
                + "        <a:p>\n"
                + "          <a:r><a:t>hello</a:t></a:r>\n"
                + "        </a:p>\n"
                + "    </p:sp>\n"
                + "  </p:cSld>\n"
                + "</p:sld>";

        String result = invokeBalanceXmlTags(xml);

        // 应插入 </p:txBody> 在 </p:sp> 之前
        assertTrue(result.contains("</p:txBody>"), "应包含修复后的 </p:txBody>");
        assertTrue(result.indexOf("</p:txBody>") < result.indexOf("</p:sp>"),
                "</p:txBody> 应在 </p:sp> 之前");
        assertValidXml(result);
    }

    @Test
    void testMissingPNvSpPrClose() throws Exception {
        // <p:nvSpPr> 缺少 </p:nvSpPr>
        String xml = "<?xml version=\"1.0\"?>\n"
                + "<p:sp xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n"
                + "  <p:nvSpPr>\n"
                + "    <p:cNvPr id=\"1\" name=\"test\"/>\n"
                + "    <p:nvPr/>\n"
                + "  <p:spPr>\n"
                + "    <a:prstGeom prst=\"rect\"/>\n"
                + "  </p:spPr>\n"
                + "</p:sp>";

        String result = invokeBalanceXmlTags(xml);

        // p:nvSpPr 的子元素被闭合后，p:nvSpPr 自身应在 </p:spPr> 之前或文档末尾被闭合
        assertTrue(result.contains("</p:nvSpPr>"), "应包含修复后的 </p:nvSpPr>");
        assertValidXml(result);
    }

    @Test
    void testNestedMissingCloses() throws Exception {
        // 多层级缺失闭合标签：a:p 和 p:txBody 都缺失
        String xml = "<?xml version=\"1.0\"?>\n"
                + "<p:sp xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n"
                + "  <p:txBody>\n"
                + "    <a:p>\n"
                + "      <a:r><a:t>text</a:t></a:r>\n"
                + "</p:sp>";

        String result = invokeBalanceXmlTags(xml);

        // 应先闭合 a:p，再闭合 p:txBody，最后闭合 p:sp
        assertTrue(result.contains("</a:p>"), "应有 </a:p>");
        assertTrue(result.contains("</p:txBody>"), "应有 </p:txBody>");
        int apClose = result.indexOf("</a:p>");
        int txBodyClose = result.indexOf("</p:txBody>");
        assertTrue(apClose < txBodyClose, "</a:p> 应在 </p:txBody> 之前");
        assertValidXml(result);
    }

    @Test
    void testAlreadyValidXmlShouldNotChange() throws Exception {
        String xml = "<?xml version=\"1.0\"?>\n"
                + "<p:sp xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n"
                + "  <p:txBody>\n"
                + "    <a:p>\n"
                + "      <a:r><a:t>text</a:t></a:r>\n"
                + "    </a:p>\n"
                + "  </p:txBody>\n"
                + "</p:sp>";

        String result = invokeBalanceXmlTags(xml);

        assertEquals(xml, result, "合法 XML 不应被修改");
    }

    @Test
    void testSelfClosingTagsPreserved() throws Exception {
        String xml = "<?xml version=\"1.0\"?>\n"
                + "<p:sp xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n"
                + "  <p:nvSpPr>\n"
                + "    <p:cNvPr id=\"1\" name=\"t\"/>\n"
                + "    <p:nvPr/>\n"
                + "  </p:nvSpPr>\n"
                + "</p:sp>";

        String result = invokeBalanceXmlTags(xml);

        assertEquals(xml, result, "合法 XML 不应被修改");
    }
}
