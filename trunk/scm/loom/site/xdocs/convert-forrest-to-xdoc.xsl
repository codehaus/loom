<?xml version='1.0' encoding='iso-8859-1'?>

<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method='xml' encoding='iso-8859-1' indent='yes'/>

    <xsl:template match="header">
        <properties>
            <title><xsl:value-of select="title"/></title>
            <xsl:apply-templates/>
        </properties>    
    </xsl:template>
    
    <xsl:template match="authors">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="person">
        <author email="{@email}"><xsl:value-of select="@name"/></author>
    </xsl:template>
    
    <xsl:template match="title"/>
    
    <xsl:template match="section">
        <section name="{title}">
            <xsl:apply-templates/>
        </section>
    </xsl:template>
    
    <xsl:template match="section[ancestor::section]">
        <subsection name="{title}">
            <xsl:apply-templates/>
        </subsection>    
    </xsl:template>
    
    <xsl:template match="link">
        <a href="{@href}"><xsl:apply-templates/></a>
    </xsl:template>
    
    <xsl:template match="code">
        <source>
            <xsl:apply-templates/>
        </source>
    </xsl:template>
    
    <xsl:template match="figure">
        <img src="{@src}" alt="{@alt}"/>
    </xsl:template>

  	<xsl:template match="node()|@*" name="identity">
    		<!-- Copy the current node -->
    		<xsl:copy>
      			<!-- Including any attributes it has and any child nodes -->
      			<xsl:apply-templates select="@*|node()"/>
    		</xsl:copy>
  	</xsl:template>
</xsl:stylesheet>