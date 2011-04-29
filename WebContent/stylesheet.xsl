<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:pp="http://www-mmt.inf.tu-dresden.de/Lehre/Sommersemester_10/Vo_WME/Uebung/material/photonpainter">
	
	<xsl:template match="/">
		<html xmlns="http://www.w3c.org/1999/xhtml">
			<head>
				<title>Photo Website</title>
			</head>
			<body>
				<h1>Photos</h1>
				<div>
					<xsl:apply-templates />
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="pp:photo">
		<div>
			<h2>
				<xsl:value-of select="@title" />
			</h2>
			<xsl:value-of select="pp:description" />
			<img>
				<xsl:attribute name="src">
					http://141.76.61.48:8103/photos?format=smallimg&amp;id=<xsl:value-of select="@id" />
				</xsl:attribute>
			</img>
		</div>
	</xsl:template>
</xsl:stylesheet>