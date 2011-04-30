<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xhtml xsl xs">

	<xsl:template match="/">
		<pp>
			<xsl:apply-templates match="*"/>
		</pp>
	</xsl:template>

	<xsl:template match="xhtml:head">
	</xsl:template>
	
	<xsl:template match="xhtml:body">
		<xsl:apply-templates match="*"/>
	</xsl:template>
	
	<xsl:template match="xhtml:h3">
	</xsl:template>

	<xsl:template match="xhtml:ul">
		<p>ul gefunden</p>
		<xsl:for-each select="document('http://141.76.61.48:8103/photos')/*">
   			<xsl:value-of select="."/>
   		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>