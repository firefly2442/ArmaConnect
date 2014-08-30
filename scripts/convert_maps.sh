#!/bin/sh

#Uses ImageMagick (convert) for conversion

#Original Windows batch scripts here:
#https://github.com/moagrius/TileView/wiki/Detailed-Setup
#https://github.com/moagrius/TileView/wiki/Creating-Tiles

create_map() {
	filename=$2
	tilesize=256
	downsamplesize=1000

	tilesfolder="tiles/"$1
	downsamplesfolder="downsamples"

	mkdir -p ${tilesfolder}
	mkdir -p ${tilesfolder}/1000/
	mkdir -p ${tilesfolder}/500/
	mkdir -p ${tilesfolder}/250/
	mkdir -p ${tilesfolder}/125
	mkdir -p ${downsamplesfolder}

	echo "Generating base images."

	convert ${filename} -monitor -resize 50% "500"${filename}
	convert ${filename} -monitor -resize 25% "250"${filename}
	convert ${filename} -monitor -resize 12.5% "125"${filename}

	echo "Generating smaller map images."

	convert ${filename} -monitor -resize ${downsamplesize}x${downsamplesize} ./${downsamplesfolder}/downsample.png

	echo "Generating tiles (this may take some time)."

	convert ${filename} -crop ${tilesize}x${tilesize} -set filename:tile "%[fx:page.x/${tilesize}]_%[fx:page.y/${tilesize}]" +repage +adjoin "./${tilesfolder}/1000/%[filename:tile].png"
	convert "500"${filename} -crop ${tilesize}x${tilesize} -set filename:tile "%[fx:page.x/${tilesize}]_%[fx:page.y/${tilesize}]" +repage +adjoin "./${tilesfolder}/500/%[filename:tile].png"
	convert "250"${filename} -crop ${tilesize}x${tilesize} -set filename:tile "%[fx:page.x/${tilesize}]_%[fx:page.y/${tilesize}]" +repage +adjoin "./${tilesfolder}/250/%[filename:tile].png"
	convert "125"${filename} -crop ${tilesize}x${tilesize} -set filename:tile "%[fx:page.x/${tilesize}]_%[fx:page.y/${tilesize}]" +repage +adjoin "./${tilesfolder}/125/%[filename:tile].png"

	#cleanup temp files
	rm "500"${filename}
	rm "250"${filename}
	rm "125"${filename}
}


create_map "Stratis" "Stratis.png"

echo "Finished."
