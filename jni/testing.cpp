#include <iostream>
#include <opencv2/highgui/highgui.hpp>
#include <vector>
#include "image.h"
#include "constants.h"
#include "result.h"

int main(int argc, char *argv[])
{
	if (argc <= 1) {
		std::cout << "Usage: ShopAndStore <PATH_TO_IMAGE>" << std::endl;
		exit(1);
	}

	std::cout << argv[1] << std::endl;;
	constants::originalImage = argv[1];
        constants::bgElimImage = "image.tiff";
        constants::dataPath = "/home/cian/Development/ShopAndStore/jni/";
	constants::dataPathFull = "/home/cian/Development/ShopAndStore/jni/tessdata/eng.traineddata";
	Image *image = new Image();
	cv::namedWindow("win");
	cv::imshow("win", image->getImage());
        cv::waitKey(0);

        std::vector<std::string> product=Result::getResult()->getProduct();
        std::vector<std::string> price=Result::getResult()->getPrice();

        for(std::vector<std::string>::iterator pdit=product.begin();pdit<product.end();pdit++){
            std::cout << "Product: " << *pdit << std::endl;
        }

        for(std::vector<std::string>::iterator prit=price.begin();prit<price.end();prit++){
            std::cout << "Price: " << *prit << std::endl;
        }
	delete image;
}